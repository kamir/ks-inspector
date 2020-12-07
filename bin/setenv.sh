#=======================================================================================================================
# Example : How to provide Cluster Credentials via ENV-Variables to the KST tool?
#=======================================================================================================================

########################################################################################################################
# Please fill in the API-KEYs and API-SECRETS for all environments and keep this data in a save place.
#-----------------------------------------------------------------------------------------------------------------------

########################################################################################################################
# This allows us to switch between Confluent cloud environments by using an ENV_VAR as a pointer.
#-----------------------------------------------------------------------------------------------------------------------
export KST_TARGET_CLUSTER_NAME=cluster_0
#export KST_TARGET_CLUSTER_NAME=cluster_1_QA
#export KST_TARGET_CLUSTER_NAME=cluster_1_PROD