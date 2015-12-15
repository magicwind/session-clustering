__author__ = 'feng'

import clusters

sessionIds, data = clusters.readSessionFile('session.csv')
clust = clusters.hcluster(data, distance=clusters.session_dissimilarity)

#clusters.printclust(clust, labels=sessionIds)
clusters.drawdendrogram(clust, sessionIds, jpeg='sessionclust.jpg')
