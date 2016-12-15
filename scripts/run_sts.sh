#!/bin/bash

DEPLOY=_ANSIBLE_REPLACED_DEPLOY_DIR_

java -Xmx1G -cp $( ls $DEPLOY/lib/itg_metrics_sts*.jar ):$DEPLOY/lib/* com.pacb.itg.metrics.sts.Main $1 $2