# -*- coding: utf-8 -*-
"""
Created on Sat Jan 13 15:12:35 2018

@author: Marwan Maalouf
"""

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import re

## main
Unique = ['NUM_STAT', 'NUM_COND', 'NUM_MODULO', 'NUM_MULT', 'NUM_DIV', 'NUM_INVOKE']
Executed = ['NUM_EXECSTAT', 'NUM_EXECCOND', 'NUM_EXECMODULO', 'NUM_EXECMULT', 'NUM_EXECDIV', 'NUM_EXECINVOKE']
analysis_columns = ['NUM_STAT', 'NUM_EXECSTAT', 'NUM_COND', 'NUM_EXECCOND', 'NUM_MODULO',
                    'NUM_EXECMODULO', 'NUM_MULT', 'NUM_EXECMULT', 'NUM_DIV', 'NUM_EXECDIV',
                    'NUM_INVOKE', 'NUM_EXECINVOKE']


def boxPlot(dataframe, title, col):
    dataframe[col].plot.box()
    plt.title(title)
    plt.xlabel('metrics')
    plt.show()

def boxPlotAll(dataframe, title):
    boxPlot(dataframe, title= 'Unique statements Propagation analysis ' + title, col=Unique)
    boxPlot(dataframe, title= 'Executed statements Propagation analysis ' + title, col=Executed)



### Get the path to the oracle csv files
print('\n\nGet all generated oracle data')
def getTestName(row, testName):
    row['TESTNAME'] = testName.split('\\')[-1].split('(')[0]
    return row

oracles = []
with open('Lang_oracles.txt', 'r') as fin:
    for line in fin:
        temp_df = pd.read_csv(line.strip()).apply(getTestName, axis=1, args=(line.strip(),))
        oracles.append(temp_df)

df = pd.concat(oracles, ignore_index=True)
df = df.dropna(axis=0)
df.info()
print(df)

#############################################################################
print('\n\nAnalysis on Strong oracles + FAIL')
print(df[analysis_columns].describe())
boxPlotAll(df, '')

##############################################################################
print('\n\nAdd version to oracles')

def split(delimiters, toSplit):
    regexPattern = '|'.join(map(re.escape, delimiters))
    return re.split(regexPattern, toSplit)

def addversion(row):
    oracleIdentifier = str(row['ORACLE'])
    oracleIdentifier = oracleIdentifier.upper()
    oracleIdentifier = split(('STRONG ORACLE ','-'), oracleIdentifier)
    row['VERSION'] = int(oracleIdentifier[1])
    return row

data = df.apply(addversion, axis=1)
print(data[['ORACLE', 'VERSION']])


##############################################################################
print('\n\nGet all failing test cases per version')

def getVersion(path):
    return path.split('\\')[-1].split('.')[0].replace('v','').replace('b','')

print('\n\nTesting')
temp_FailingTests = []
with open('Lang_FailingTests.txt', 'r') as fin:
    for line in fin:
        tempLine = line.strip()
        info = open(tempLine, 'r').read().strip()
        info = info.replace('::', '.')
        tests = info.split(',')
        for test in tests:
            temp_FailingTests.append([int(getVersion(tempLine)), test])

failingTests = pd.DataFrame(temp_FailingTests, columns=['VERSION', 'TESTNAME'])
failingTests.info()
print(failingTests)
##############################################################################
print('\n\nAdd fail attribute to oracles')

def getPassorFail(row):
    row['FAIL'] = row['TESTNAME'] in failingTests.loc[
            failingTests['VERSION'] == row['VERSION']
            ]['TESTNAME'].unique()
    return row

data = data.apply(getPassorFail, axis= 1)
data.info()
print(data.head())

data.to_csv('output.csv')
print('\n\nGenerated output file: \'output.csv\'')
##############################################################################
print('\n\nAnalysis on Strong oracles + FAIL')
fail_data = data.loc[data['FAIL'] == True]
print(fail_data[analysis_columns].describe())

boxPlotAll(fail_data, 'Failing tests')

fail_data.to_csv('Fail.csv')
fail_data[analysis_columns].describe().to_csv('Fail_Analysis.csv')
##############################################################################
print('\n\nAnalysis on Strong oracles + PASS')
pass_data = data.loc[data['FAIL'] == False]
print(pass_data[analysis_columns].describe())

boxPlotAll(pass_data, 'Failing tests')

pass_data.to_csv('Pass.csv')
pass_data[analysis_columns].describe().to_csv('Pass_Analysis.csv')
##############################################################################


