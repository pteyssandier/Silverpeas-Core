create index R_24_FK on SR_UNINSTITEMS (SR_PACKAGE)
go

create index SR_UNINSTITEMS_I1 on SR_UNINSTITEMS (SR_PACKAGE, SR_ACTION_TAG, SR_ITEM_ORDER)
go

create index RELATION_46_FK on SR_SCRIPTS (SR_ITEM_ID)
go

create index SR_SCRIPTS_I1 on SR_SCRIPTS (SR_ITEM_ID, SR_SEQ_NUM)
go

create index IN_DEPENDENCIES_1 on SR_DEPENDENCIES (SR_PACKAGE, SR_PKDEPENDENCY)
go

