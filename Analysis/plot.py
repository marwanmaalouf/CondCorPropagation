# -*- coding: utf-8 -*-
"""
Created on Sat Jan 13 15:12:35 2018

@author: Marwan Maalouf
"""

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import re

## main
Unique = ['NUM_STAT', 'NUM_COND', 'NUM_MODULO', 'NUM_MULT', 'NUM_DIV', 'NUM_INVOKE']
Executed = ['NUM_EXECSTAT', 'NUM_EXECCOND', 'NUM_EXECMODULO', 'NUM_EXECMULT', 'NUM_EXECDIV', 'NUM_EXECINVOKE']
analysis_columns = ['NUM_STAT', 'NUM_EXECSTAT', 'NUM_COND', 'NUM_EXECCOND', 'NUM_MODULO',
                    'NUM_EXECMODULO', 'NUM_MULT', 'NUM_EXECMULT', 'NUM_DIV', 'NUM_EXECDIV',
                    'NUM_INVOKE', 'NUM_EXECINVOKE']



df = pd.read_csv('output.csv')
passing = df.loc[df['FAIL'] == False]
failing = df.loc[df['FAIL'] == True]

df['PROP'] = df['FAIL'].apply(lambda x: 'Failing\ntests' if x==True else 'CC')


## function for setting the colors of the box plots pairs
#def setBoxColors(bp):
#    plt.setp(bp['boxes'][0], color='blue')
#    plt.setp(bp['caps'][0], color='blue')
#    plt.setp(bp['caps'][1], color='blue')
#    plt.setp(bp['whiskers'][0], color='blue')
#    plt.setp(bp['whiskers'][1], color='blue')
#    plt.setp(bp['fliers'][0], color='blue')
#    plt.setp(bp['fliers'][1], color='blue')
#    plt.setp(bp['medians'][0], color='blue')
#
#    plt.setp(bp['boxes'][1], color='red')
#    plt.setp(bp['caps'][2], color='red')
#    plt.setp(bp['caps'][3], color='red')
#    plt.setp(bp['whiskers'][2], color='red')
#    plt.setp(bp['whiskers'][3], color='red')
#    plt.setp(bp['fliers'][2], color='red')
#    plt.setp(bp['fliers'][3], color='red')
#    plt.setp(bp['medians'][1], color='red')
#
#fig = plt.figure()
#ax = plt.axes()
#plt.hold(True)
#
## first boxplot pair
#bp = plt.boxplot([passing['NUM_STAT'].as_matrix(), failing['NUM_STAT'].as_matrix()], 
#                  positions = [1, 2], widths = 0.6)
#setBoxColors(bp)
#
## second boxplot pair
#bp = boxplot(B, positions = [4, 5], widths = 0.6)
#setBoxColors(bp)
#
## thrid boxplot pair
#bp = boxplot(C, positions = [7, 8], widths = 0.6)
#setBoxColors(bp)
#
## set axes limits and labels
#plt.xlim(0,9)
#plt.ylim(0,9)
#ax.set_xticklabels(['A', 'B', 'C'])
#ax.set_xticks([1.5, 4.5, 7.5])
#
## draw temporary red and blue lines and use them to create a legend
#hB, = plot([1,1],'b-')
#hR, = plot([1,1],'r-')
#plt.legend((hB, hR),('Apples', 'Oranges'))
#hB.set_visible(False)
#hR.set_visible(False)
#
#plt.savefig('boxcompare.png')
#plt.show()
#
#
#
#

analysis_columns = ['NUM_STAT', 'NUM_EXECSTAT', 'NUM_COND', 'NUM_EXECCOND', 'NUM_MODULO',
                    'NUM_EXECMODULO', 'NUM_MULT', 'NUM_EXECMULT', 'NUM_DIV', 'NUM_EXECDIV',
                    'NUM_INVOKE', 'NUM_EXECINVOKE']
graphTitles = ['Unique statements executed',
               '                                                                      Statements executed',
               'Unique conditional branches executed',
               '                                                                      Conditional branches executed',
               'Unique modulo statements executed',
               'Modulo statements executed',
               'Unique multiplication statements executed',
               '                                                                      Multiplication statements executed',
               'Unique division statements executed',
               '                                                                      Division statements executed',
               'Unique invoke statements executed',
               '                                                                      Invoke statements executed',
               ]
plt.rcParams.update({'font.size': 22})
#for i in range(len(analysis_columns)):
#     plt.figure(figsize= (20, 10), dpi=150)
#     ax= sns.boxplot(x=analysis_columns[i], order=["CC", "Failing\ntests"], y="PROP", data=df, orient="h", palette="deep", width=0.3)
#     ax.set_xlabel(graphTitles[i], fontsize= 26)
#     ax.set_ylabel('')
#     plt.savefig('Fig_{0}.png'.format(i))


# If we were to simply plot pts, we'd lose most of the interesting
# details due to the outliers. So let's 'break' or 'cut-out' the y-axis
# into two portions - use the top (ax) for the outliers, and the bottom
# (ax2) for the details of the majority of our data
f, (ax, ax2) = plt.subplots(1, 2, sharey=True, figsize= (20, 10), dpi=150)
i =11
# plot the same data on both axes
ax= sns.boxplot(ax=ax, x=analysis_columns[i], order=["CC", "Failing\ntests"], y="PROP", data=df, orient="h", palette="deep", width=0.3)
ax2= sns.boxplot(ax=ax2, x=analysis_columns[i], order=["CC", "Failing\ntests"], y="PROP", data=df, orient="h", palette="deep", width=0.3)


# zoom-in / limit the view to different portions of the data
ax2.set_xlim(0.4*10**3, 1*10**3)  # outliers only
ax.set_xlim(0, 0.13*10**3)  # most of the data

# hide the spines between ax and ax2
ax.spines['right'].set_visible(False)
ax2.spines['left'].set_visible(False)
ax.yaxis.tick_left()
ax.tick_params(labelright='off')  # don't put tick labels at the top
ax2.tick_params(labelright='off')  # don't put tick labels at the top
ax2.yaxis.tick_right()
ax2.set_ylabel('')
ax2.set_xlabel('')
ax.set_ylabel('')
# This looks pretty good, and was fairly painless, but you can get that
# cut-out diagonal lines look with just a bit more work. The important
# thing to know here is that in axes coordinates, which are always
# between 0-1, spine endpoints are at these locations (0,0), (0,1),
# (1,0), and (1,1).  Thus, we just need to put the diagonals in the
# appropriate corners of each of our axes, and so long as we use the
# right transform and disable clipping.

d = .015  # how big to make the diagonal lines in axes coordinates
# arguments to pass to plot, just so we don't keep repeating them
kwargs = dict(transform=ax.transAxes, color='k', clip_on=False)
ax.plot((1 - d, 1 + d), (1-d, 1+d), **kwargs)        # top-left diagonal
ax.plot((1 - d, 1 + d), (-d, +d), **kwargs)  # top-right diagonal

kwargs.update(transform=ax2.transAxes)  # switch to the bottom axes
ax2.plot((-d, +d), (1 - d, 1 + d), **kwargs)  # bottom-left diagonal
ax2.plot((- d, + d), (- d, + d), **kwargs)  # bottom-right diagonal


f.subplots_adjust(wspace=0.03)
# What's cool about this is that now if we vary the distance between
# ax and ax2 via f.subplots_adjust(hspace=...) or plt.subplot_tool(),
# the diagonal lines will move accordingly, and stay right at the tips
# of the spines they are 'breaking'
ax.set_xlabel(graphTitles[i], fontsize= 26)
#ax.set_xticklabels(['0', '5', '10', '15', '20'])
#ax2.set_xticklabels(['40', '50', '60', '70', '80', '90', '100'])
plt.xticks(ha='left')

plt.savefig('Lang_{0}.png'.format(i))

#plt.show()