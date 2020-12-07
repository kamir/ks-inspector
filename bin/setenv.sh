#=======================================================================================================================
# Example : How to provide Cluster Credentials via ENV-Variables to the KST tool?
#=======================================================================================================================

########################################################################################################################
# Please fill in the API-KEYs and API-SECRETS for all environments and keep this data in a save place.
#-----------------------------------------------------------------------------------------------------------------------
export KST_CLUSTER_API_KEY_cluster_0=QEHNVESKVXWEHAFY
export KST_CLUSTER_API_SECRET_cluster_0=JIzNSR+tC1wSWJ74/F4h/Aypekd/eLKEx+H0mHbFj+02WL+5NeJn6W7YSAVwRQN4
export KST_SR_API_KEY_cluster_0=IYHWTF6QXUGSJH5P
export KST_SR_API_SECRET_cluster_0=JyCbUFbsOs2XtW4t6t3alHFaSe50GVG2y14MnFy7jqJf8H0N10W2ySWgmJeOJ08o

export KST_CLUSTER_API_KEY_cluster_1_QA=NI2WPUTM5AUSIVYF
export KST_CLUSTER_API_SECRET_cluster_1_QA=06GvNt7xwpVTSzX0gk9xC2xnMXY/CiA0YqJcvkVRUYmG4LIVFkyBsAxWQbuUsged
export KST_SR_API_KEY_cluster_1_QA=W6RZQDGAMJIGI5UO
export KST_SR_API_SECRET_cluster_1_QA=o8EfgGqsoKPD2JhoAmMXXo4XLNR4QME+a/H+kwX1pk95O0gvOT5N3Hr7L78csjsp

export KST_CLUSTER_API_KEY_cluster_1_PROD=KP63DKMDVOWMNAWF
export KST_CLUSTER_API_SECRET_cluster_1_PROD=ks/nYvpX7OqVQndc++AiyC5eFTHo1izsB2li0XcuCr/vjl0AVH5FW7ir80q3Og1T
export KST_SR_API_KEY_cluster_1_PROD=7P7RXVBHN25Z2MSU
export KST_SR_API_SECRET_cluster_1_PROD=cSXIsY7EL8TEGB2ZwLrBib/HSmrX8j3eIsdmY6NzsOmkYwCuGOa6uYx3hjHq83SG

########################################################################################################################
# This allows us to switch between Confluent cloud environments by using an ENV_VAR as a pointer.
#-----------------------------------------------------------------------------------------------------------------------
#export KST_TARGET_CLUSTER_NAME=cluster_0
#export KST_TARGET_CLUSTER_NAME=cluster_1_QA
export KST_TARGET_CLUSTER_NAME=cluster_1_PROD