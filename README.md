Esse repositório contém a implementação de uma API de gerenciamento de apólices de seguro, seguindo os requisitos do desafio proposto pelo Itaú. A API permite criar, consultar e gerenciar apólices de seguro, integrando-se com uma API externa de fraude para validação que está utilizando um Mock Server para emular sua resposta. 

As requisiçoes de mock da api de fraude e POST de exemplo da colletion Postman sao com base na documentação do desafio.
### Pré-requisitos
- Docker
- Java 17
- Maven

## Rodando o projeto local
Para rodar o projeto localmente, você pode usar o script `build.ps1` que está localizado na raiz do projeto. Este script permite compilar, testar e executar o projeto, além de gerenciar containers Docker.

Você pode usar o PowerShell para executar os comandos do script. O script é projetado para ser executado em um ambiente Windows, mas caso use outro sistema pode rodar o `docker-compose` diretamente.

#### Ajuda - Comandos PS1 - Windows
 ```powershell
 # Para ver a ajuda
.\build.ps1 help
 ```

## Teste via Postman
Para testar a API manualmente, você pode usar o Postman . O arquivo `dev-env/Desafio Itaú - Insurance API.postman_collection.json` contem uma coleção de requisições que você pode importar no Postman. Esta coleção inclui exemplos de requisições para criar e consultar.

### Alteração no mock - API de Fraude
Para alterar o mock da API de Fraude, você pode editar o arquivo `dev-env/mock-fraud-api/config/expectations.json`. Este arquivo contém as expectativas de requisições e respostas que o mock deve atender. Após editar, você pode reiniciar o mock server com os comandos do `docker-compose` ou `build.ps1` para reiniciar o sistema e aplicar as mudanças.

## Decisões técnicas  - Solução

### Requisitos Funcionais
A documentação detalha qual o cilo de vida de uma solicitação de apólice de seguro, desde a criação até o cancelamento. Porém, existem requisitos conflitantes e decisões tomadas que vou listar a seguir como justificativas para algumas escolhas de implementação:
1. **Status de Apólice**:
Segundo o documento (cópia disponível na raiz do projeto) o status de uma apolice de `Recebida` só pode ir para `Validado` ou `Cancelado`, quando na realidade ela pode ir para `Rejeitado` também, pois no passo 2. é descrito que ser `Validado`, é necessário após a consulta a api de fraudes e as condiçoes satisfeitas a solicitação passa para o estado `Validado`, caso
contrário `Rejeitado`. Ou seja, ela iria de `Recebido` para `Rejeitado`.
2. **Status Pendente e consumo de eventos externos**:
A documentação diz que o status `Pendente` é um estado intermediário, mas não está claro se ele deve ser usado para aguardar eventos externos tendo em vista que em nenhuma outra parte da documentação menciona exemplos de eventos tanto para confirmação de pagamento quanto autorização da subscrição, bem como quais o contrato desses eventos como é feito a resposta da API de Fraude. No entanto, para simplificar o fluxo e evitar estados intermediários desnecessários, optei por apenas passar esse status como uma transição entre o `Validado` e o `Aprovado`. 
3. **Cancelamento de Apólice:**
Embora o cancelamento de apólice seja um requisito, como não há um consumo de eventos externos que exigem o status `Pendente` seja um estado transitorio, todas as apolices ou são `Aprovada` ou `Rejeitada` estados estes que não podem ir para `Cancelado`, por essa razão não foi implementado esse endpoint de cancelamento.
### Padrões de Design - Implementados
1. **Service Layer** (Camada de Serviço):
    - Sendo usado pelas classes (`FraudService`, `InsuranceService`). Centralizando a lógica de negócio nos serviços para manter os controllers com pouca regra.
2. **Repository Pattern**:
    - Implementado com o uso de `InsuranceRepository`. Repositórios para abstrair o acesso ao banco de dados.
3. **Builder Pattern**:
    - Para objetos com muitos atributos e muitas vezes complexo, como `InsurancePolicyReponse`, foi utilizado um builder para facilitar a construção.
4. **DTO (Data Transfer Object)**:
    - `InsurancePolicyRequest` e `InsurancePolicyResponse` usando DTOs para transferir dados entre camadas não acoplando contratos de Request e Response dentro do domínio.
5. **Observer Pattern**:
    - Mudanças de status em `InsurancePolicy` estão notificando serviços externos quando o status muda.
### Melhorias:
1. **Factory Pattern**:
    - Usar factorys para criar objetos complexos, como `InsurancePolicy` a partir de `InsurancePolicyRequest`. Isso ajudaria a encapsular a lógica de criação.
2. **Chain of Responsibility**:
    - Para processar validações ou regras de negócio em sequência, como validações de apólice, implementar uma chain of responsability.
3. **Documentação OpenAPI**:
- Implementar documentação OpenAPI para a API, facilitando o entendimento e uso da API por outros desenvolvedores.
4. **Kubernetes**:
- Um k8s seria interessante para orquestrar os containers, principalmente em produção, para garantir alta disponibilidade e escalabilidade. Pensando nisso criei endpoints de `/health` que pode ser usado para verificar a saúde do serviço e integrá-lo com ferramentas de monitoramento como Prometheus e Grafana.
5. **Maquina de Estados**:
- Implementar uma máquina de estados para gerenciar os estados da apólice de forma mais robusta e flexível, permitindo adicionar novos estados e transições facilmente.