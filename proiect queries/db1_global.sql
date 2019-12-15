create table CLIENT (client_id NUMBER(5), nume VARCHAR2(50), prenume VARCHAR2(50), email VARCHAR2(100), tip_client VARCHAR2(10));

ALTER TABLE CLIENT
ADD CONSTRAINT pk_client PRIMARY KEY (client_id);

CREATE SEQUENCE sqnc START WITH 1;

insert into CLIENT (client_id, nume, prenume, email, tip_client) values(sqnc.nextval, 'Georgescu', 'Marius', 'gmarius@gmail.com', 'fizic');
insert into CLIENT (client_id, nume, prenume, email, tip_client) values(sqnc.nextval, 'Stefanescu', 'Dan', 'sdan@gmail.com', 'fizic');

insert into CLIENT (client_id, nume, prenume, email, tip_client) values(sqnc.nextval, 'Cobalcescu', 'Daniel', 'cdaniel@gmail.com', 'juridic');
insert into CLIENT (client_id, nume, prenume, email, tip_client) values(sqnc.nextval, 'Rainea', 'Ionut', 'ionut_rainea@gmail.com', 'juridic');

select * from client

---------------------------

CREATE TABLE CATEGORIE ("CATEGORIE_ID" NUMBER(5,0), 
                            "NUME_CATEGORIE" VARCHAR2(40), 
                            CONSTRAINT "CATEGORIE_PK" PRIMARY KEY ("CATEGORIE_ID"))
                            
    
CREATE TABLE COMANDA ("COMANDA_ID" NUMBER(5,0), 
                            "OBSERVATII" VARCHAR2(100 BYTE), 
                            "CLIENT_ID" NUMBER(5,0), 
                            CONSTRAINT "COMANDA_PK" PRIMARY KEY ("COMANDA_ID"),
                            CONSTRAINT "COMANDA_CLIENT_ID" 
                            FOREIGN KEY ("CLIENT_ID")
                            REFERENCES "CLIENT" ("CLIENT_ID"));
                            

CREATE TABLE PRODUS (	"PRODUS_ID" NUMBER(5,0), 
                        "CATEGORIE_ID" NUMBER(5,0), 
                        "COMANDA_ID" NUMBER(5,0), 
                        "NUME_PRODUS" VARCHAR2(30 BYTE), 
                        "STOC_RAMAS" NUMBER(5,0), 
                        "CULOARE" VARCHAR2(15 BYTE), 
                        "PRET" NUMBER(6,2), 
                        CONSTRAINT "PRODUS_PK" PRIMARY KEY ("PRODUS_ID")
                        CONSTRAINT "PRODUS_COMANDA_ID" FOREIGN KEY ("COMANDA_ID")
                        REFERENCES "COMANDA" ("COMANDA_ID"), 
                        CONSTRAINT "PRODUS_CATEGORIE_ID" FOREIGN KEY ("CATEGORIE_ID")
                        REFERENCES "CATEGORIE" ("CATEGORIE_ID"));
 
CREATE TABLE FACTURA ("FACTURA_ID" NUMBER(5,0), 
                        "TOTAL_PRET" NUMBER(8,2), 
                        "COMANDA_ID" NUMBER(5,0), 
                        CONSTRAINT "FACTURA_PK" PRIMARY KEY ("FACTURA_ID")
                        CONSTRAINT "FACTURA_COMANDA_ID" FOREIGN KEY ("COMANDA_ID")
                        REFERENCES "COMANDA" ("COMANDA_ID"));

CREATE TABLE ISTORIC_PRET ("ISTORIC_PRET_ID" NUMBER(5,0), 
                            "PRODUS_ID" NUMBER(5,0), 
                            "DATA" TIMESTAMP, 
                            PRET NUMBER(6,2),
                            CONSTRAINT "ISTORIC_PRET_PK" PRIMARY KEY ("ISTORIC_PRET_ID")
                            CONSTRAINT "ISTORIC_PRET_PRODUS_ID" FOREIGN KEY ("PRODUS_ID")
                            REFERENCES "DB1_GLOBAL"."PRODUS" ("PRODUS_ID"));
                            

CREATE TABLE MODALITATE_PLATA ("MODALITATE_PLATA_ID" NUMBER(5,0), 
                                "NUME_MODALITATE" VARCHAR2(30), 
                                CONSTRAINT "MODALITATE_PLATA_PK" PRIMARY KEY ("MODALITATE_PLATA_ID"));
 
CREATE MATERIALIZED VIEW LOG ON MODALITATE_PLATA
WITH rowid,primary key;

CREATE MATERIALIZED VIEW LOG ON CATEGORIE
WITH rowid,primary key;

select * from categorie

------------------------------

 

