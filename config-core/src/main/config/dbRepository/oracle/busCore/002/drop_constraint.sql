ALTER TABLE ST_AccessLevel DROP CONSTRAINT UN_AccessLevel_1;

ALTER TABLE ST_User DROP CONSTRAINT UN_User_1;
ALTER TABLE ST_User DROP CONSTRAINT UN_User_2;
ALTER TABLE ST_User DROP CONSTRAINT FK_User_1;

ALTER TABLE ST_Group DROP CONSTRAINT UN_Group_1;
ALTER TABLE ST_Group DROP CONSTRAINT UN_Group_2;
ALTER TABLE ST_Group DROP CONSTRAINT FK_Group_1;

ALTER TABLE ST_Group_User_Rel DROP CONSTRAINT FK_Group_User_Rel_1;
ALTER TABLE ST_Group_User_Rel DROP CONSTRAINT FK_Group_User_Rel_2;

ALTER TABLE ST_Space DROP CONSTRAINT UN_Space_1;
ALTER TABLE ST_Space DROP CONSTRAINT FK_Space_1;
ALTER TABLE ST_Space DROP CONSTRAINT FK_Space_2;

ALTER TABLE ST_ComponentInstance DROP CONSTRAINT UN_ComponentInstance_1;
ALTER TABLE ST_ComponentInstance DROP CONSTRAINT FK_ComponentInstance_1;
ALTER TABLE ST_ComponentInstance DROP CONSTRAINT FK_ComponentInstance_2;

ALTER TABLE ST_Instance_Data DROP CONSTRAINT UN_Instance_Data_1;
ALTER TABLE ST_Instance_Data DROP CONSTRAINT FK_Instance_Data_1;

ALTER TABLE ST_UserRole DROP CONSTRAINT UN_UserRole_1;
ALTER TABLE ST_UserRole DROP CONSTRAINT FK_UserRole_1;

ALTER TABLE ST_UserRole_User_Rel DROP CONSTRAINT FK_UserRole_User_Rel_1;
ALTER TABLE ST_UserRole_User_Rel DROP CONSTRAINT FK_UserRole_User_Rel_2;

ALTER TABLE ST_UserRole_Group_Rel DROP CONSTRAINT FK_UserRole_Group_Rel_1;
ALTER TABLE ST_UserRole_Group_Rel DROP CONSTRAINT FK_UserRole_Group_Rel_2;

ALTER TABLE ST_SpaceUserRole DROP CONSTRAINT UN_SpaceUserRole_1;
ALTER TABLE ST_SpaceUserRole DROP CONSTRAINT FK_SpaceUserRole_1;

ALTER TABLE ST_SpaceUserRole_User_Rel DROP CONSTRAINT FK_SpaceUserRole_User_Rel_1;
ALTER TABLE ST_SpaceUserRole_User_Rel DROP CONSTRAINT FK_SpaceUserRole_User_Rel_2;

ALTER TABLE ST_SpaceUserRole_Group_Rel DROP CONSTRAINT FK_SpaceUserRole_Group_Rel_1;
ALTER TABLE ST_SpaceUserRole_Group_Rel DROP CONSTRAINT FK_SpaceUserRole_Group_Rel_2;

ALTER TABLE ST_UserSetType DROP CONSTRAINT UN_UserSetType_1;

ALTER TABLE ST_UserSet DROP CONSTRAINT FK_UserSet_2;

ALTER TABLE ST_UserSet_UserSet_Rel DROP CONSTRAINT FK_UserSet_UserSet_Rel_1;
ALTER TABLE ST_UserSet_UserSet_Rel DROP CONSTRAINT FK_UserSet_UserSet_Rel_2;
ALTER TABLE ST_UserSet_UserSet_Rel DROP CONSTRAINT no_cycle;

ALTER TABLE ST_UserSet_User_Rel DROP CONSTRAINT FK_UserSet_User_Rel_1;
ALTER TABLE ST_UserSet_User_Rel DROP CONSTRAINT FK_UserSet_User_Rel_2;

ALTER TABLE DomainSP_Group DROP CONSTRAINT UN_DomainSP_Group_1;
ALTER TABLE DomainSP_Group DROP CONSTRAINT FK_DomainSP_Group_1;

ALTER TABLE DomainSP_User DROP CONSTRAINT UN_DomainSP_User_1;

ALTER TABLE DomainSP_Group_User_Rel DROP CONSTRAINT FK_DomainSP_Group_User_Rel_1;
ALTER TABLE DomainSP_Group_User_Rel DROP CONSTRAINT FK_DomainSP_Group_User_Rel_2;

ALTER TABLE ST_AccessLevel DROP CONSTRAINT PK_AccessLevel;
ALTER TABLE ST_User DROP CONSTRAINT PK_User;
ALTER TABLE ST_Group DROP CONSTRAINT PK_Group;
ALTER TABLE ST_Group_User_Rel DROP CONSTRAINT PK_Group_User_Rel;
ALTER TABLE ST_Space DROP CONSTRAINT PK_Space;
ALTER TABLE ST_ComponentInstance DROP CONSTRAINT PK_ComponentInstance;
ALTER TABLE ST_Instance_Data DROP CONSTRAINT PK_Instance_Data;
ALTER TABLE ST_UserRole DROP CONSTRAINT PK_UserRole;
ALTER TABLE ST_UserRole_User_Rel DROP CONSTRAINT PK_UserRole_User_Rel;
ALTER TABLE ST_UserRole_Group_Rel DROP CONSTRAINT PK_UserRole_Group_Rel;
ALTER TABLE ST_SpaceUserRole DROP CONSTRAINT PK_SpaceUserRole;
ALTER TABLE ST_SpaceUserRole_User_Rel DROP CONSTRAINT PK_SpaceUserRole_User_Rel;
ALTER TABLE ST_SpaceUserRole_Group_Rel DROP CONSTRAINT PK_SpaceUserRole_Group_Rel;
ALTER TABLE ST_UserSetType DROP CONSTRAINT PK_UserSetType;
ALTER TABLE ST_UserSet DROP CONSTRAINT PK_UserSet;
ALTER TABLE ST_UserSet_UserSet_Rel DROP CONSTRAINT PK_UserSet_UserSet_Rel;
ALTER TABLE ST_UserSet_User_Rel DROP CONSTRAINT PK_UserSet_User_Rel;
ALTER TABLE DomainSP_Group DROP CONSTRAINT PK_DomainSP_Group;
ALTER TABLE DomainSP_User DROP CONSTRAINT PK_DomainSP_User;
ALTER TABLE ST_Domain DROP CONSTRAINT PK_ST_Domain;
ALTER TABLE DomainSP_Group_User_Rel DROP CONSTRAINT PK_DomainSP_Group_User_Rel;
