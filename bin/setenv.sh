#=======================================================================================================================
# Example : How to provide Cluster Credentials via ENV-Variables to the KST tool?
#=======================================================================================================================

########################################################################################################################
# Please fill in the API-KEYs and API-SECRETS for all environments and keep this data in a save place.
#-----------------------------------------------------------------------------------------------------------------------

#
# cluster_2 login ...
#
export KST_CLUSTER_API_KEY_cluster_2=AG2X57TIVLOSA2UF
export KST_CLUSTER_API_SECRET_cluster_2=128ALrmHYSzfyrmffHIj9HwgtEBeyjHZ2qQidBXnd/tPbpLRZL1mojIH97G05Har
export KST_SR_API_KEY_cluster_2=4TRMPKIJYHVENEPL
export KST_SR_API_SECRET_cluster_2=yNfXyhSlnRtmrl4VePfLI8KJRnDaROBMA7Rn+WV5SXQU8DLGyqiCc+fSPPVnpfqP

#
# cluster_0 login ...
#
# --- a local unsecure cluster does not require any API-Keys

########################################################################################################################
# This allows us to switch between Confluent cloud environments by using an ENV_VAR as a pointer.
#-----------------------------------------------------------------------------------------------------------------------
export KST_TARGET_CLUSTER_NAME=cluster_0
export KST_TARGET_CLUSTER_NAME=cluster_2
