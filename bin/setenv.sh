#=======================================================================================================================
# Example : How to provide Cluster Credentials via ENV-Variables to the KST tool?
#=======================================================================================================================

########################################################################################################################
# Please fill in the API-KEYs and API-SECRETS for all environments and keep this data in a save place.
#-----------------------------------------------------------------------------------------------------------------------
export KST_CLUSTER_API_KEY_cluster_0=SBYTDHIKGRB4ALBY
export KST_CLUSTER_API_SECRET_cluster_0=c4VHZzx5BouA2ko4cnj3sdsTn4gU3vfDXb+cCoCZ4kL44ssE+p3nUQ6cdg091fDs
export KST_SR_API_KEY_cluster_0=DZ43N5K7WQ7OQDCB
export KST_SR_API_SECRET_cluster_0=opH/3yrzPhGqpGxrP5xB13UCFqdya72wbWf96YmNDPJr2hJt6C5sku70r2Opmnfz

########################################################################################################################
# This allows us to switch between Confluent cloud environments by using an ENV_VAR as a pointer.
#-----------------------------------------------------------------------------------------------------------------------
export KST_TARGET_CLUSTER_NAME=cluster_0
#export KST_TARGET_CLUSTER_NAME=PROD-cluster-101
