
CREATE TABLE IF NOT EXISTS usr 
(
	usr_id SERIAL,
	usr_name TEXT NOT NULL,
	usr_pwd TEXT NOT NULL,
	usr_created TIMESTAMP NOT NULL,
	usr_enabled BOOLEAN NOT NULL,
	PRIMARY KEY (usr_id),
	UNIQUE (usr_name)
);

CREATE TABLE IF NOT EXISTS auth 
(
	auth_id SERIAL,
	auth_name TEXT NOT NULL,
	auth_usr INT,
	PRIMARY KEY(auth_id),
	UNIQUE (auth_name, auth_usr),
	FOREIGN KEY (auth_usr) REFERENCES usr(usr_id)
);
