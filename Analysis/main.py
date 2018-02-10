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
    plt.xticks(rotation= 'vertical')
    plt.show()
    plt.savefig(title)


def boxPlotAll(dataframe, title):
    boxPlot(dataframe, title= 'Unique statements Propagation analysis ' + title, col=Unique)
    boxPlot(dataframe, title= 'Executed statements Propagation analysis ' + title, col=Executed)




### Get the path to the oracle csv files
print('\n\nGet all generated oracle data')
def getTestName(row, testName, projectName):
    row['TESTNAME'] = testName.split('\\')[-1].split('(')[0]
    row['PROJECT'] = projectName
    return row

oracles = []
with open('Lang_oracles.txt', 'r') as fin:
    for line in fin:
        temp_df = pd.read_csv(line.strip()).apply(getTestName, axis=1, args=(line.strip(),'LANG',))
        oracles.append(temp_df)


df = pd.concat(oracles, ignore_index=True)
df = df.dropna(axis=0)
df[analysis_columns] = df[analysis_columns].astype(int)
df.info()
#print(df)

#############################################################################
print('\n\nAnalysis on Strong oracles')
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
    row['VERSION'] = int(oracleIdentifier[1].strip())
    return row

data = df.apply(addversion, axis=1)
#print(data[['ORACLE', 'VERSION']])


##############################################################################
print('\n\nGet all failing test cases per version')

def getVersion(path):
    return path.split('\\')[-1].split('.')[0].replace('v','').replace('b','')


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
#print(failingTests)
##############################################################################
'''
MM: Data cleaning: In version 43, 62 and 65 of Lang, the strong oracles are 
set to be printed only once (instead of being printed on every occurence) to 
avoid buffer overflow exception. Thus the propagation metrics captured in 
these entries represent the propagation from the first time the oracle gets 
trigerred and are not representative of what is captured in this experiment.
'''
data = data[ (data.VERSION != 43) | (data.PROJECT != 'LANG') ]
data = data[ (data.VERSION != 62) | (data.PROJECT != 'LANG') ]
data = data[ (data.VERSION != 65) | (data.PROJECT != 'LANG') ]
data.reset_index(drop=True, inplace=True)
##############################################################################
print('\n\nAdd fail attribute to oracles')

def getPassorFail(row):
    row['FAIL'] = row['TESTNAME'] in failingTests.loc[
            failingTests['VERSION'] == row['VERSION']
            ]['TESTNAME'].unique()
    return row

data = data.apply(getPassorFail, axis= 1)
data.info()
#print(data.head())

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

boxPlotAll(pass_data, 'Passing tests')

pass_data.to_csv('Pass.csv')
pass_data[analysis_columns].describe().to_csv('Pass_Analysis.csv')
##############################################################################
print('\n\nComparative analysis on Strong oracles')
allFrames = [fail_data, pass_data, data]
allFramesTitle = ['Failed test', 'CC', 'All']
for i in range(len(analysis_columns)):
    f, axarr = plt.subplots(1, 3)
    for j in range(len(allFrames)):
        allFrames[j][[analysis_columns[i]]].boxplot(ax=axarr[j])
        axarr[j].set_title(allFramesTitle[j])
        # Fine-tune figure; hide x ticks for top plots and y ticks for right plots
        #plt.setp([a.get_xticklabels() for a in axarr[0, :]], visible=False)
        #plt.setp([a.get_yticklabels() for a in axarr[:, 2]], visible=False)
        plt.show()




