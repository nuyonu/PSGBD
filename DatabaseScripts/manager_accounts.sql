CREATE OR REPLACE PACKAGE manager_accounts IS
  PROCEDURE add_user(p_username IN VARCHAR2, p_password IN VARCHAR2, p_e_mail VARCHAR2, p_rank CHAR, p_birth_date DATE,
                     p_origin_country VARCHAR2);
  PROCEDURE delete_user(p_username IN users.username%TYPE);

  FUNCTION username_already_exists(p_username IN VARCHAR2) RETURN BOOLEAN;
  FUNCTION email_already_exists(p_email IN VARCHAR2) RETURN BOOLEAN;
  FUNCTION login(p_username IN VARCHAR2, p_password IN VARCHAR2) RETURN BOOLEAN;
END manager_accounts;
/
CREATE OR REPLACE PACKAGE BODY manager_accounts IS

    key_bytes_raw RAW(32) := '576D5A7134743777217A25432A462D4A404E635266556A586E3272357538782F';
    encryption_type PLS_INTEGER := DBMS_CRYPTO.ENCRYPT_AES256 + DBMS_CRYPTO.CHAIN_CBC + DBMS_CRYPTO.PAD_PKCS5;
    iv_raw RAW(16) := 'A939EFCAE6F3FF008227D10E7BC19E97';
    
  PROCEDURE add_user(p_username IN VARCHAR2, p_password IN VARCHAR2, p_e_mail VARCHAR2, p_rank CHAR, p_birth_date DATE,
                     p_origin_country VARCHAR2) AS
    encrypted_raw RAW(64); -- stores encrypted binary text
--    --encryption key
--    key_bytes_raw RAW(32) := '576D5A7134743777217A25432A462D4A404E635266556A586E3272357538782F';
--    -- total encryption type
--    encryption_type PLS_INTEGER := DBMS_CRYPTO.ENCRYPT_AES256 + DBMS_CRYPTO.CHAIN_CBC + DBMS_CRYPTO.PAD_PKCS5;
--    iv_raw RAW(16) := 'A939EFCAE6F3FF008227D10E7BC19E97';
    encrypted_password CHAR(32);
  BEGIN
    IF username_already_exists(p_username) AND email_already_exists(p_e_mail) THEN
      DBMS_OUTPUT.PUT_LINE('Utilizatorul ' || p_username || ' exista deja');
    ELSE
      encrypted_raw := DBMS_CRYPTO.ENCRYPT
        (
          src => UTL_I18N.STRING_TO_RAW(p_password, 'AL32UTF8'),
          typ => encryption_type,
          key => key_bytes_raw,
          iv => iv_raw
        );
    DBMS_OUTPUT.PUT_LINE(TO_CHAR(encrypted_raw));
    encrypted_password := TO_CHAR(encrypted_raw);
      INSERT INTO users (username, password, e_mail, rank, birth_date, origin_country)
      VALUES (p_username, encrypted_password, p_e_mail, p_rank, p_birth_date, p_origin_country);
      DBMS_OUTPUT.PUT_LINE('Utilizatorul ' || p_username || ' a fost adaugat cu succes in baza de date');
    END IF;
  END add_user;
  
  PROCEDURE delete_user(p_username IN users.username%TYPE) AS
    BEGIN
        DELETE FROM users WHERE p_username = username;
    END delete_user;

  FUNCTION username_already_exists(p_username IN VARCHAR2) RETURN BOOLEAN AS
    v_count NUMBER(1);
  BEGIN
    SeLeCt COUNT(users.username) INTO v_count FROM users WHERE username = p_username;
    IF v_count > 0 THEN
      RETURN TRUE;
    ELSE
      RETURN FALSE;
    END IF;
  END username_already_exists;
  
  FUNCTION email_already_exists(p_email IN VARCHAR2) RETURN BOOLEAN AS
    v_count NUMBER(1);
  BEGIN
    SeLeCt COUNT(users.username) INTO v_count FROM users WHERE E_MAIL = p_email;
    IF v_count > 0 THEN
      RETURN TRUE;
    ELSE
      RETURN FALSE;
    END IF;
  END email_already_exists;
  FUNCTION login(p_username IN VARCHAR2, p_password IN VARCHAR2) RETURN BOOLEAN AS
    v_decoded_password VARCHAR2(32);
    decrypted_raw RAW(64);
    v_encrypted_raw RAw(64);
  BEGIN
    IF NOT username_already_exists(p_username) THEN
        RETURN FALSE;
    ELSE
        SELECT users.password INTO v_encrypted_raw FROM users WHERE username = p_username;
        decrypted_raw := DBMS_CRYPTO.DECRYPT
      (
         src => v_encrypted_raw,
         typ => encryption_type,
         key => key_bytes_raw,
         iv  => iv_raw
      );
      v_decoded_password := UTL_I18N.RAW_TO_CHAR (decrypted_raw, 'AL32UTF8');
        IF(v_decoded_password = p_password) THEN
            RETURN TRUE;
        ELSE
            RETURN FALSE;
        END IF;
    END IF;
  END login;
END manager_accounts;
/
BEGIN
    IF manager_accounts.login('nuyonu', 'parola') THEN
        DBMS_OUTPUT.PUT_LINE('TRUE');
    ELSE
        DBMS_OUTPUT.PUT_LINE('FALSE');
    END IF;
END;
/