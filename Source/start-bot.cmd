@echo off

REM input: <role> <first id> <last id> <port>

FOR /L %%i IN (%2,1,%3) DO start java -jar bot.jar -cp nl.dcs.app.BotProgram -role %1 -id %%i -server "rmi://localhost:%4/"


