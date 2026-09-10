# ⚔️ ProjetoCa - Sistema de RPG em Java (POO)

Um sistema de RPG desenvolvido em Java com foco na aplicação de conceitos avançados de Programação Orientada a Objetos (POO) e persistência de dados. Este projeto foi construído como parte da grade acadêmica da FATEC, demonstrando a integração de uma arquitetura baseada em herança com um banco de dados serverless (SQLite) utilizando Java Reflection.

## 🚀 Tecnologias e Ferramentas
* **Java:** Linguagem principal do projeto.
* **SQLite (JDBC):** Banco de dados relacional serverless para armazenamento local (`banco_rpg.db`).
* **Java Reflection:** Utilizado para inspeção dinâmica de classes em tempo de execução.
* **Eclipse IDE:** Ambiente de desenvolvimento.

## 🧠 Conceitos de POO Aplicados
O sistema foi modelado para refletir boas práticas de engenharia de software e design de código:
* **Herança e Polimorfismo:** Estrutura hierárquica robusta para as entidades do jogo. A superclasse abstrata `Personagem` distribui características vitais para subclasses como `Guerreiro`, `Mago` e `Cacador`. O mesmo ocorre com o sistema de `Item` (ex: `Arma`, `Pocao`).
* **Sobrecarga de Construtores:** Lógica separada para inicialização de entidades recém-criadas no jogo (ID autoincremental) versus entidades carregadas da persistência.
* **DAO Dinâmico (GenericDAO):** Eliminação de código repetitivo (boilerplate). Um único DAO é capaz de realizar o CRUD de qualquer entidade do jogo gerando queries SQL de forma dinâmica em tempo de execução.

## ⚙️ Arquitetura de Persistência (GenericDAO)
O grande diferencial deste projeto é o seu sistema de persistência inteligente. Através do arquivo `GenericDAO.java`, o sistema consegue:
1. Ler dinamicamente todos os atributos de uma classe (incluindo os atributos herdados da superclasse).
2. Ignorar campos não mapeados no banco (como Listas de itens e modificadores temporários de estado).
3. Gerar instruções dinâmicas de `INSERT` e `SELECT` no banco de dados SQLite sem que o desenvolvedor precise escrever SQL hardcoded para cada nova classe do jogo.
4. Ler os metadados do banco (`ResultSetMetaData`) para imprimir registros dinamicamente no console.

## 🛠️ Como Executar o Projeto

### Pré-requisitos
* Ter o [JDK](https://www.oracle.com/java/technologies/downloads/) instalado.
* Fazer o download do driver JDBC do SQLite (`sqlite-jdbc.jar`).

### Passos
1. Faça o clone deste repositório:
   git clone [https://github.com/dragonfirsty/ProjetoCa.git](https://github.com/dragonfirsty/ProjetoCa.git)
2. Importe o projeto na sua IDE de preferência (recomendado: Eclipse).
3. Crie uma pasta lib na raiz do projeto, cole o arquivo sqlite-jdbc.jar dentro dela e adicione a biblioteca ao Build Path do projeto.
4.Execute o arquivo Main.java. O sistema criará o banco de dados banco_rpg.db automaticamente na raiz do projeto e fará as validações de CRUD no console.
