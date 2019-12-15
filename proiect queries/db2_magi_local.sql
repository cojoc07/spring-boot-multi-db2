create table client_juridic
as
select *
from   db1_global.client@dblink
where  tip_client='juridic';

select * from client_juridic

desc client_juridic;
---------------------
alter table client_juridic
add constraint client_juridic_email_unique unique(email);

ALTER TABLE client_juridic MODIFY (nume NOT NULL);

ALTER TABLE client_juridic MODIFY (prenume NOT NULL);

ALTER TABLE client_juridic MODIFY (tip_client NOT NULL);

------------------------  constraint de unique email local + in tabela fragmentata orizontal


CREATE OR REPLACE TRIGGER t_client_email_unique_global
BEFORE INSERT ON client_juridic
FOR EACH ROW
DECLARE
   nr NUMBER (6);
BEGIN
SELECT COUNT(*) INTO nr
FROM   client_fizic@dblink
WHERE  email=:NEW.email;
IF (nr<>0) THEN RAISE_APPLICATION_ERROR (-20002,
               'emailul exista deja in cealalta baza de date');
END IF;
END;

-------------------------
--creare tabel COMANDA_J pe local db 2
CREATE TABLE "COMANDA_J" ("COMANDA_ID" NUMBER(5,0), 
                            "OBSERVATII" VARCHAR2(100 BYTE), 
                            "CLIENT_ID" NUMBER(5,0), 
                            CONSTRAINT "COMANDA_J_PK" PRIMARY KEY ("COMANDA_ID"),
                            CONSTRAINT "COMANDA_J_CLIENT_ID" 
                            FOREIGN KEY ("CLIENT_ID")
                            REFERENCES "CLIENT_JURIDIC" ("CLIENT_ID"));
-------------------------
ALTER TABLE comanda_j MODIFY (observatii NOT NULL);


--------------------------------------------
--creaza view materializat pentru db2 local

CREATE MATERIALIZED VIEW v_mat_modalitate_plata
REFRESH COMPLETE
START WITH SYSDATE
NEXT SYSDATE+1
WITH PRIMARY KEY
AS
SELECT *
FROM DB1_GLOBAL.modalitate_plata@dblink;

select * from v_mat_modalitate_plata;
-----------------------------------------------
-- se creaza factura aferenta pers JURIDICE

CREATE TABLE FACTURA_J ("FACTURA_ID" NUMBER(5,0), 
                        "TOTAL_PRET" NUMBER(8,2), 
                        "COMANDA_ID" NUMBER(5,0), 
                        CONSTRAINT "FACTURA_J_PK" PRIMARY KEY ("FACTURA_ID"),
                        CONSTRAINT "FACTURA_COMANDA_ID_J" FOREIGN KEY ("COMANDA_ID")
                        REFERENCES "COMANDA_J" ("COMANDA_ID"));
                        
ALTER TABLE FACTURA_J MODIFY (TOTAL_PRET NOT NULL);

ALTER TABLE FACTURA_J ADD modalitate_plata number(5); 

ALTER TABLE FACTURA_J
ADD CONSTRAINT factura_j_mod_plata
FOREIGN KEY (modalitate_plata)
REFERENCES v_mat_modalitate_plata (modalitate_plata_id);

SELECT * from FACTURA_J    


--------------------------------------------
--creaza view materializat pentru db1 local

CREATE MATERIALIZED VIEW v_mat_categorie
REFRESH COMPLETE
START WITH SYSDATE
NEXT SYSDATE+1
WITH PRIMARY KEY
AS
SELECT *
FROM DB1_GLOBAL.categorie@dblink;

select * from v_mat_categorie;


------------------------------ CREATE PRODUS TABLE

CREATE TABLE PRODUS_J (	PRODUS_ID NUMBER(5,0), 
                        CATEGORIE_ID NUMBER(5,0), 
                        COMANDA_ID NUMBER(5,0), 
                        NUME_PRODUS VARCHAR2(30), 
                        CULOARE VARCHAR2(15), 
                        PRET NUMBER(6,2), 
                        CONSTRAINT PRODUS_J_PK PRIMARY KEY (PRODUS_ID),
                        CONSTRAINT PRODUS_COMANDA_ID_FK_J 
                        FOREIGN KEY (COMANDA_ID)
                        REFERENCES COMANDA_J (COMANDA_ID), 
                        CONSTRAINT PRODUS_CATEGORIE_ID_FK_J
                        FOREIGN KEY (CATEGORIE_ID)
                        REFERENCES v_mat_categorie (CATEGORIE_ID));

alter table PRODUS_J
add constraint produs_j_nume_produs_unique unique(nume_produs);

------------------------  constraint de unique nume_produs local + in tabela fragmentata orizontal

CREATE OR REPLACE TRIGGER t_nume_produs_unique_global
BEFORE INSERT ON produs_j
FOR EACH ROW
DECLARE
   nr NUMBER (6);
BEGIN
SELECT COUNT(*) INTO nr
FROM   produs_f@dblink
WHERE  nume_produs=:NEW.nume_produs;
IF (nr<>0) THEN RAISE_APPLICATION_ERROR (-20002,
               'numele de produs exista deja in cealalta baza de date');
END IF;
END;

---------------------------------
-- fragmentare verticala pt tabela istoric pret

CREATE TABLE ISTORIC_PRET (ISTORIC_PRET_ID NUMBER(5,0), 
                            DATA TIMESTAMP, 
                            PRET NUMBER(6,2),
                            CONSTRAINT ISTORIC_PRET_PK PRIMARY KEY (ISTORIC_PRET_ID)
                           );