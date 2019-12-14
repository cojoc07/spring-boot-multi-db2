create table client_fizic
as
select *
from   db1_global.client
where  tip_client='fizic';

select * from client_fizic

--------------------------


CREATE OR REPLACE VIEW "V_CLIENT" ("CLIENT_ID", "NUME", "PRENUME", "EMAIL", "TIP_CLIENT") 
AS 
  select "CLIENT_ID","NUME","PRENUME","EMAIL","TIP_CLIENT" from client_fizic
    union all
  select "CLIENT_ID","NUME","PRENUME","EMAIL","TIP_CLIENT" from client_juridic@dblink;

--------------------------

CREATE OR REPLACE TRIGGER t_client
INSTEAD OF INSERT or DELETE ON v_client
FOR EACH ROW
BEGIN
   if inserting then
      if :new.tip_client = 'fizic' then
         insert into client_fizic
         values (:new.client_id, :new.nume, :new.prenume, :new.email, :new.tip_client);
      else
         insert into client_juridic@dblink
         values (:new.client_id, :new.nume, :new.prenume, :new.email, :new.tip_client);
      end if;
   else if deleting then
      if :old.tip_client = 'fizic' then
          delete from client_fizic
          where  client_id = :old.client_id;
      else
          delete from client_juridic@dblink
          where  client_id = :old.client_id;
      end if;
    end if;
   end if;
END;

--------------------------------
INSERT INTO v_client VALUES (db1_global.sqnc.nextval,'Ionut', 'Cacat', 'naruto@gmail.com', 'fizic');

UPDATE v_client SET email = 'cozonac@gmail.com' WHERE email = 'naruto@gmail.com'

DELETE FROM v_client WHERE email = 'naruto@gmail.com'
 
SELECT * FROM v_client

DESC client_fizic
---------------------
alter table client_fizic
add constraint client_fizic_email_unique unique(email);

ALTER TABLE client_fizic MODIFY (nume NOT NULL);

ALTER TABLE client_fizic MODIFY (prenume NOT NULL);

ALTER TABLE client_fizic MODIFY (tip_client NOT NULL);


------------------------  constraint de unique email local + in tabela fragmentata orizontal

CREATE OR REPLACE TRIGGER t_client_email_unique_global
BEFORE INSERT ON client_fizic
FOR EACH ROW
DECLARE
   nr NUMBER (6);
BEGIN
SELECT COUNT(*) INTO nr
FROM   client_juridic@dblink
WHERE  email=:NEW.email;
IF (nr<>0) THEN RAISE_APPLICATION_ERROR (-20002,
               'emailul exista deja in cealalta baza de date');
END IF;
END;

-------------------------
--creare tabel COMANDA_F pe local db 1
CREATE TABLE "COMANDA_F" ("COMANDA_ID" NUMBER(5,0), 
                            "OBSERVATII" VARCHAR2(100 BYTE), 
                            "CLIENT_ID" NUMBER(5,0), 
                            CONSTRAINT "COMANDA_F_PK" PRIMARY KEY ("COMANDA_ID"),
                            CONSTRAINT "COMANDA_F_CLIENT_ID" 
                            FOREIGN KEY ("CLIENT_ID")
                            REFERENCES "CLIENT_FIZIC" ("CLIENT_ID"));
--------------------------
CREATE OR REPLACE VIEW "V_COMANDA" ("COMANDA_ID", "OBSERVATII", "CLIENT_ID") 
AS 
  select * from comanda_f
    union all
  select * from comanda_j@dblink;
-------------------------
ALTER TABLE comanda_f MODIFY (observatii NOT NULL);

CREATE OR REPLACE TRIGGER t_comanda
INSTEAD OF INSERT or DELETE ON v_comanda
FOR EACH ROW
DECLARE
    tip_client VARCHAR2(10) := null;
BEGIN

   if inserting then
      select c.tip_client into tip_client from v_client c where c.client_id = :new.client_id;
      if tip_client = 'fizic' then
         insert into comanda_f
         values (:new.comanda_id, :new.observatii, :new.client_id);
      else
         insert into comanda_j@dblink
         values (:new.comanda_id, :new.observatii, :new.client_id);
      end if;
   else if deleting then
      select c.tip_client into tip_client from v_client c where c.client_id = :old.client_id;
      if tip_client = 'fizic' then
          delete from comanda_f
          where  comanda_id = :old.comanda_id;
      else
          delete from comanda_j@dblink
          where  comanda_id = :old.comanda_id;
      end if;
    end if;
   end if;
END;

select * from comanda_f;
select * from comanda_j@dblink;
select * from v_comanda;
select * from v_client;

--------------------------------------------
--creaza view materializat pentru db1 local

CREATE MATERIALIZED VIEW v_mat_modalitate_plata
REFRESH COMPLETE
START WITH SYSDATE
NEXT SYSDATE+1
WITH PRIMARY KEY
AS
SELECT *
FROM DB1_GLOBAL.modalitate_plata;

select * from v_mat_modalitate_plata;
-----------------------------------------------
-- se creaza factura aferenta pers FIZICE

CREATE TABLE FACTURA_F ("FACTURA_ID" NUMBER(5,0), 
                        "TOTAL_PRET" NUMBER(8,2), 
                        "COMANDA_ID" NUMBER(5,0), 
                        CONSTRAINT "FACTURA_F_PK" PRIMARY KEY ("FACTURA_ID"),
                        CONSTRAINT "FACTURA_COMANDA_ID_F" FOREIGN KEY ("COMANDA_ID")
                        REFERENCES "COMANDA_F" ("COMANDA_ID"));

ALTER TABLE FACTURA_F MODIFY (TOTAL_PRET NOT NULL);

ALTER TABLE FACTURA_F ADD modalitate_plata number(5); 

ALTER TABLE FACTURA_F
ADD CONSTRAINT factura_f_mod_plata
FOREIGN KEY (modalitate_plata)
REFERENCES v_mat_modalitate_plata (modalitate_plata_id);

SELECT * from FACTURA_F       
--------------------------
CREATE OR REPLACE VIEW "V_FACTURA" ("FACTURA_ID", "TOTAL_PRET", "COMANDA_ID") 
AS 
  select * from FACTURA_F
    union all
  select * from FACTURA_J@dblink;
  
--------------------------------
-- trigger pt operatiuni pe view

CREATE OR REPLACE TRIGGER t_factura
INSTEAD OF INSERT or DELETE ON v_factura
FOR EACH ROW
DECLARE
    tip_client VARCHAR2(10) := null;
BEGIN

   if inserting then
      select c.tip_client into tip_client 
      from v_client c left join v_comanda vc ON c.client_id = vc.client_id 
      where vc.comanda_id = :new.comanda_id;
      
      if tip_client = 'fizic' then
         insert into factura_f
         values (:new.factura_id, :new.total_pret, :new.comanda_id);
      else
         insert into factura_j@dblink
         values (:new.factura_id, :new.total_pret, :new.comanda_id);
      end if;
   else if deleting then
      select c.tip_client into tip_client 
      from v_client c left join v_comanda vc ON c.client_id = vc.client_id 
      where vc.comanda_id = :new.comanda_id;
      
      if tip_client = 'fizic' then
          delete from factura_f
          where  factura_id = :old.factura_id;
      else
          delete from factura_j@dblink
          where  factura_id = :old.factura_id;
      end if;
    end if;
   end if;
END;

DESC FACTURA_F
select * from v_client 
SELECT * FROM v_factura
select * from factura_j@dblink
SELECT * FROM v_comanda
INSERT INTO v_factura VALUES (db1_global.sqnc.nextval, 96, 42);  
INSERT INTO v_factura VALUES (db1_global.sqnc.nextval, 150, 46); 

--------------------------------------------
--creaza view materializat pentru db1 local

CREATE MATERIALIZED VIEW v_mat_categorie
REFRESH COMPLETE
START WITH SYSDATE
NEXT SYSDATE+1
WITH PRIMARY KEY
AS
SELECT *
FROM DB1_GLOBAL.categorie;

select * from v_mat_categorie;

------------------------------ CREATE PRODUS TABLE

CREATE TABLE PRODUS_F (	PRODUS_ID NUMBER(5,0), 
                        CATEGORIE_ID NUMBER(5,0), 
                        COMANDA_ID NUMBER(5,0), 
                        NUME_PRODUS VARCHAR2(30), 
                        CULOARE VARCHAR2(15), 
                        PRET NUMBER(6,2), 
                        CONSTRAINT PRODUS_F_PK PRIMARY KEY (PRODUS_ID),
                        CONSTRAINT PRODUS_COMANDA_ID_FK_F 
                        FOREIGN KEY (COMANDA_ID)
                        REFERENCES COMANDA_F (COMANDA_ID), 
                        CONSTRAINT PRODUS_CATEGORIE_ID_FK_F 
                        FOREIGN KEY (CATEGORIE_ID)
                        REFERENCES v_mat_categorie (CATEGORIE_ID));
                        
alter table PRODUS_F
add constraint produs_f_nume_produs_unique unique(nume_produs);

------------------------  constraint de unique nume_produs local + in tabela fragmentata orizontal

CREATE OR REPLACE TRIGGER t_nume_produs_unique_global
BEFORE INSERT ON produs_f
FOR EACH ROW
DECLARE
   nr NUMBER (6);
BEGIN
SELECT COUNT(*) INTO nr
FROM   produs_j@dblink
WHERE  nume_produs=:NEW.nume_produs;
IF (nr<>0) THEN RAISE_APPLICATION_ERROR (-20002,
               'numele de produs exista deja in cealalta baza de date');
END IF;
END;

-------------------------- cream view pe produs

CREATE OR REPLACE VIEW "V_PRODUS" (PRODUS_ID, CATEGORIE_ID, COMANDA_ID, NUME_PRODUS, CULOARE, PRET) 
AS 
  select * from PRODUS_F
    union all
  select * from PRODUS_J@dblink;
  
DESC v_PRODUs

ALTER 

SELECT * FROM V_PRODUS
--------------------------------
-- trigger pt operatiuni pe view

CREATE OR REPLACE TRIGGER t_produs
INSTEAD OF INSERT or DELETE ON v_produs
FOR EACH ROW
DECLARE
    tip_client VARCHAR2(10) := null;
BEGIN

   if inserting then
      select c.tip_client into tip_client 
      from v_client c 
      left join v_comanda vc ON c.client_id = vc.client_id
      where vc.comanda_id = :new.comanda_id;
      
      if tip_client = 'fizic' then
         insert into produs_f
         values (:new.PRODUS_ID, :new.CATEGORIE_ID, :new.comanda_id, :new.NUME_PRODUS, :new.CULOARE, :new.PRET);
      else
         insert into produs_j@dblink
         values (:new.PRODUS_ID, :new.CATEGORIE_ID, :new.comanda_id, :new.NUME_PRODUS, :new.CULOARE, :new.PRET);
      end if;
   else if deleting then
      select c.tip_client into tip_client 
      from v_client c 
      left join v_comanda vc ON c.client_id = vc.client_id
      where vc.comanda_id = :old.comanda_id;
      
      if tip_client = 'fizic' then
          delete from produs_f
          where  produs_id = :old.produs_id;
      else
          delete from produs_j@dblink
          where  produs_id = :old.produs_id;
      end if;
    end if;
   end if;
END;

select * from v_produs
select * from v_comanda
select * from v_mat_categorie

select * from produs_j@dblink
insert into v_produs values (db1_global.sqnc.nextval, 70, 42, 'Placa video Nvidia RTX 2080', 'Verde', 2100);
insert into v_produs values (db1_global.sqnc.nextval, 72, 46, 'Hartie Xerox', 'Alb', 23);

----------------------------
-- fragmentare verticala pt tabela istoric pret

CREATE TABLE ISTORIC_PRET (ISTORIC_PRET_ID NUMBER(5,0), 
                            PRODUS_ID NUMBER(5,0), 
                            CONSTRAINT ISTORIC_PRET_PK PRIMARY KEY (ISTORIC_PRET_ID)
                            );
                            
CREATE OR REPLACE VIEW "V_ISTORIC_PRET" ("ISTORIC_PRET_ID", "PRODUS_ID", "DATA", "PRET") 
AS 
  select ilocal.istoric_pret_id, ilocal.produs_id, iremote.data, iremote.pret from istoric_pret ilocal
  left join istoric_pret@dblink iremote ON ilocal.istoric_pret_id = iremote.istoric_pret_id;
  

  
select * from v_istoric_pret;  

--------------------------------
-- trigger pt operatiuni pe view

CREATE OR REPLACE TRIGGER t_istoric_pret
INSTEAD OF INSERT or DELETE ON v_istoric_pret
FOR EACH ROW
DECLARE
    produseGasite NUMBER(5) := NULL; 
BEGIN

   if inserting then
       select count(*) into produseGasite from v_produs where produs_id = :new.produs_id;
           if produseGasite <> 1 then
                   RAISE_APPLICATION_ERROR(-20003, 'Ati incercat sa inserati un istoric de produs pentru un produs inexistent.');
           end if;
         insert into istoric_pret
         values (:new.istoric_pret_id, :new.produs_id);
         
         insert into istoric_pret@dblink
         values (:new.istoric_pret_id, :new.data, :new.pret);
      
   else if deleting then
        delete from istoric_pret
        where istoric_pret_id = :old.istoric_pret_id;
        delete from istoric_pret@dblink
        where istoric_pret_id = :old.istoric_pret_id;
    end if;
   end if;
END;
  
  
select * from v_produs
select * from v_istoric_pret
select * from istoric_pret@dblink
insert into v_istoric_pret values (db1_global.sqnc.nextval, 99, CURRENT_TIMESTAMP, 2400);

select * from v_produs pr
delete from v_produs where produs_id = 99;
