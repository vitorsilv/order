# TODO
- Teste Kadrop
- Kafka envio
- Kafka Consumo - Maquina de estados
- Testes
- Métricas
- Logs
- Atualizar PS1

## Seria bom
- jenkins
- kube

## Rodando o projeto local

### Comandos PS1

 ```powershell
 # Para ver a ajuda
.\build.ps1 help

# Comandos básicos
.\build.ps1 build
.\build.ps1 run
.\build.ps1 test

# Comandos Docker
.\build.ps1 docker-build
.\build.ps1 compose-up
.\build.ps1 logs

# Limpeza e reinício
.\build.ps1 clean
.\build.ps1 restart

# Verificar ambiente
.\build.ps1 check-env
 ```