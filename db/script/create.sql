
    alter table ASSUJETTI 
        drop 
        foreign key FK_ASSUJETTI_PERSON;

    alter table ASSUJETTI 
        drop 
        foreign key FK_ASSUJETTI_ADRESSE;

    alter table ASSUJETTI 
        drop 
        foreign key FK_ASSUJETTI_USER;

    alter table BIENTAXE 
        drop 
        foreign key FK_BIENTAXE_ASSUJETTI;

    alter table BIENTAXE 
        drop 
        foreign key FK_BIENTAXE_ADRESSE;

    alter table COMMENTAIRE 
        drop 
        foreign key FK_COMMENTAIRE_ATTACHMENT;

    alter table COMMENTAIRE 
        drop 
        foreign key FK_COMMENTAIRE_DECLARATION;

    alter table COMMENTAIRE 
        drop 
        foreign key FK_COMMENTAIRE_USER;

    alter table DECLARATION 
        drop 
        foreign key FK_DECLARATION_BIENTAXE;

    alter table DECLARATION 
        drop 
        foreign key FK_DECLARATION_ADRESSE;

    alter table GUESTEXEMPTIONS 
        drop 
        foreign key FK_GUESTEXEMPTIONS_DECLARATION;

    alter table LOG 
        drop 
        foreign key FK_LOG_USER;

    alter table USER 
        drop 
        foreign key FK_USER_PERSON;

    drop table if exists ADRESSE;

    drop table if exists ASSUJETTI;

    drop table if exists ATTACHMENT;

    drop table if exists BIENTAXE;

    drop table if exists COMMENTAIRE;

    drop table if exists COMMUNE;

    drop table if exists COUNTRY;

    drop table if exists DECLARATION;

    drop table if exists GUESTEXEMPTIONS;

    drop table if exists LOCALITE;

    drop table if exists LOG;

    drop table if exists PERSON;

    drop table if exists ROLES;

    drop table if exists TARIF;

    drop table if exists USER;

    create table ADRESSE (
        ADRESSE_ID bigint not null auto_increment,
        VERSION integer not null,
        ADRESSE varchar(255),
        EMAIL varchar(255),
        LOCALITE varchar(255) not null,
        NO varchar(20),
        NPA varchar(15) not null,
        PAYS_CODE varchar(2) not null,
        RUE varchar(255),
        TELEPHONE varchar(30),
        primary key (ADRESSE_ID)
    ) ENGINE=InnoDB;

    create table ASSUJETTI (
        ASJ_ID integer not null auto_increment,
        VERSION integer not null,
        UPDATED char(1) not null,
        ADRESSE_DE_CORRESPONDENCE_ID bigint not null unique,
        PERSON_ID bigint not null unique,
        USER_ID bigint unique,
        primary key (ASJ_ID)
    ) ENGINE=InnoDB;

    create table ATTACHMENT (
        ATTACHMENT_ID bigint not null auto_increment,
        VERSION integer not null,
        CONTENT longblob,
        FILENAME varchar(255),
        primary key (ATTACHMENT_ID)
    ) ENGINE=InnoDB;

    create table BIENTAXE (
        BIEN_ID bigint not null auto_increment,
        VERSION integer not null,
        COMMUNE_CODE varchar(255) not null,
        DECLARATION_TYPE varchar(255) not null,
        EGID varchar(255),
        EWID varchar(255),
        ETABLISSEMENT varchar(255),
        PERIODICITE_CODE varchar(255) not null,
        ADRESSE_DE_OBJECT_ID bigint not null,
        ASJ_ID integer not null,
        primary key (BIEN_ID)
    ) ENGINE=InnoDB;

    create table COMMENTAIRE (
        COMMENT_ID bigint not null auto_increment,
        VERSION integer not null,
        MESSAGE varchar(2500) not null,
        TIMESTAMP datetime,
        ATTACHMENT_ID bigint unique,
        DEC_ID integer not null,
        USER_ID bigint not null,
        primary key (COMMENT_ID)
    ) ENGINE=InnoDB;

    create table COMMUNE (
        COMMUNE_ID bigint not null auto_increment,
        CODE varchar(50),
        NAME varchar(50),
        primary key (COMMUNE_ID)
    ) ENGINE=InnoDB;

    create table COUNTRY (
        COUNTRY_ID bigint not null auto_increment,
        COUNTRY_ISO_CODE varchar(2) not null,
        COUNTRY varchar(255) not null,
        LANG_ISO_CODE varchar(2) not null,
        primary key (COUNTRY_ID),
        unique (COUNTRY_ISO_CODE, LANG_ISO_CODE)
    ) ENGINE=InnoDB;

    create table DECLARATION (
        DEC_ID integer not null auto_increment,
        VERSION integer not null,
        PERIODICITE_CODE varchar(255) not null,
        AJUST_COMMENT varchar(255),
        AJUST_VALUE double precision,
        DEPART_DATE datetime,
        DUE_DATE datetime not null,
        ESTIMATION bigint,
        EXONERATION varchar(6) not null,
        FISCALE_DATE datetime not null,
        LAST_MODIFICATION_DATE datetime not null,
        LOCATION char(1),
        NOM varchar(255),
        PRENOM varchar(255),
        STATUS varchar(50) not null,
        SUBMISSION_DATE datetime,
        TAILLE char(1),
        USER_COMMENT varchar(2500),
        ADRESSE_DE_FACTURATION_ID bigint not null,
        BIEN_ID bigint not null,
        primary key (DEC_ID),
        unique (BIEN_ID, FISCALE_DATE)
    ) ENGINE=InnoDB;

    create table GUESTEXEMPTIONS (
        EX_ID bigint not null auto_increment,
        VERSION integer not null,
        EXEMPTION_TYPE varchar(255) not null,
        HOTES integer,
        NUITS integer,
        RESIDENTIEL integer,
        DEC_ID integer,
        primary key (EX_ID),
        unique (DEC_ID, EXEMPTION_TYPE)
    ) ENGINE=InnoDB;

    create table LOCALITE (
        LOCALITE_ID bigint not null auto_increment,
        NPA varchar(255),
        LOCALITE varchar(255),
        primary key (LOCALITE_ID)
    ) ENGINE=InnoDB;

    create table LOG (
        ENTRY_ID bigint not null auto_increment,
        VERSION integer not null,
        ENTITY_ID bigint not null,
        MESSAGE varchar(2500) not null,
        MESSAGE_TYPE varchar(255) not null,
        TIMESTAMP datetime not null,
        USER_ID bigint not null,
        primary key (ENTRY_ID)
    ) ENGINE=InnoDB;

    create table PERSON (
        PERSON_ID bigint not null auto_increment,
        VERSION integer not null,
        NOM varchar(255),
        ORGANISATION varchar(255),
        PRENOM varchar(255),
        primary key (PERSON_ID)
    ) ENGINE=InnoDB;

    create table ROLES (
        ROLE_ID bigint not null auto_increment,
        VERSION integer not null,
        PERMISSION varchar(255) not null,
        ROLE varchar(10) not null,
        primary key (ROLE_ID),
        unique (ROLE, PERMISSION)
    ) ENGINE=InnoDB;

    create table TARIF (
        TARIF_ID bigint not null auto_increment,
        VERSION integer not null,
        COEFFICIENT_RESIDENCE double precision not null,
        DATE_DEBUT datetime not null unique,
        DATE_FIN datetime not null,
        LOCATAIRE2P double precision not null,
        LOCATAIRE3P double precision not null,
        MAXLOCATAIRE2P double precision not null,
        MAXLOCATAIRE3P double precision not null,
        MAX_RESIDENCE double precision not null,
        MIN_RESIDENCE double precision not null,
        NUIT_CHAMBRE double precision not null,
        NUIT_HOTEL double precision not null,
        NUIT_INSTITUT double precision not null,
        NUIT_CAMPING double precision not null,
        RESIDENTIEL_CAMPING double precision not null,
        RESIDENTIEL_INSTITUT double precision not null,
        primary key (TARIF_ID)
    ) ENGINE=InnoDB;

    create table USER (
        USER_ID bigint not null auto_increment,
        VERSION integer not null,
        ACTIVATED char(1) not null,
        ADMIN_CREATED char(1) not null,
        ARCAM char(1) not null,
        COMMUNE_CODE varchar(255),
        EMAIL varchar(255) not null unique,
        LAST_LOG_ON datetime,
        PASSWORD varchar(255) not null,
        TELEPHONE varchar(30) not null,
        USER_TYPE varchar(10) not null,
        USERNAME varchar(30) not null unique,
        VALIDATED char(1) not null,
        PERSON_ID bigint not null,
        primary key (USER_ID)
    ) ENGINE=InnoDB;

    alter table ASSUJETTI 
        add index FK_ASSUJETTI_PERSON (PERSON_ID), 
        add constraint FK_ASSUJETTI_PERSON 
        foreign key (PERSON_ID) 
        references PERSON (PERSON_ID);

    alter table ASSUJETTI 
        add index FK_ASSUJETTI_ADRESSE (ADRESSE_DE_CORRESPONDENCE_ID), 
        add constraint FK_ASSUJETTI_ADRESSE 
        foreign key (ADRESSE_DE_CORRESPONDENCE_ID) 
        references ADRESSE (ADRESSE_ID);

    alter table ASSUJETTI 
        add index FK_ASSUJETTI_USER (USER_ID), 
        add constraint FK_ASSUJETTI_USER 
        foreign key (USER_ID) 
        references USER (USER_ID);

    alter table BIENTAXE 
        add index FK_BIENTAXE_ASSUJETTI (ASJ_ID), 
        add constraint FK_BIENTAXE_ASSUJETTI 
        foreign key (ASJ_ID) 
        references ASSUJETTI (ASJ_ID);

    alter table BIENTAXE 
        add index FK_BIENTAXE_ADRESSE (ADRESSE_DE_OBJECT_ID), 
        add constraint FK_BIENTAXE_ADRESSE 
        foreign key (ADRESSE_DE_OBJECT_ID) 
        references ADRESSE (ADRESSE_ID);

    alter table COMMENTAIRE 
        add index FK_COMMENTAIRE_ATTACHMENT (ATTACHMENT_ID), 
        add constraint FK_COMMENTAIRE_ATTACHMENT 
        foreign key (ATTACHMENT_ID) 
        references ATTACHMENT (ATTACHMENT_ID);

    alter table COMMENTAIRE 
        add index FK_COMMENTAIRE_DECLARATION (DEC_ID), 
        add constraint FK_COMMENTAIRE_DECLARATION 
        foreign key (DEC_ID) 
        references DECLARATION (DEC_ID);

    alter table COMMENTAIRE 
        add index FK_COMMENTAIRE_USER (USER_ID), 
        add constraint FK_COMMENTAIRE_USER 
        foreign key (USER_ID) 
        references USER (USER_ID);

    alter table DECLARATION 
        add index FK_DECLARATION_BIENTAXE (BIEN_ID), 
        add constraint FK_DECLARATION_BIENTAXE 
        foreign key (BIEN_ID) 
        references BIENTAXE (BIEN_ID);

    alter table DECLARATION 
        add index FK_DECLARATION_ADRESSE (ADRESSE_DE_FACTURATION_ID), 
        add constraint FK_DECLARATION_ADRESSE 
        foreign key (ADRESSE_DE_FACTURATION_ID) 
        references ADRESSE (ADRESSE_ID);

    alter table GUESTEXEMPTIONS 
        add index FK_GUESTEXEMPTIONS_DECLARATION (DEC_ID), 
        add constraint FK_GUESTEXEMPTIONS_DECLARATION 
        foreign key (DEC_ID) 
        references DECLARATION (DEC_ID);

    alter table LOG 
        add index FK_LOG_USER (USER_ID), 
        add constraint FK_LOG_USER 
        foreign key (USER_ID) 
        references USER (USER_ID);

    alter table USER 
        add index FK_USER_PERSON (PERSON_ID), 
        add constraint FK_USER_PERSON 
        foreign key (PERSON_ID) 
        references PERSON (PERSON_ID);

    alter table DECLARATION AUTO_INCREMENT = 9000000;