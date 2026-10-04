# ProjetoCa - Sistema de RPG em Java

Sistema de RPG desenvolvido em Java para aplicar conceitos de Programacao Orientada a Objetos, heranca, polimorfismo, Reflection, Generics, Annotations e persistencia com SQLite.

## Tecnologias

- Java 8 ou superior
- SQLite por JDBC
- Java Reflection
- Generics
- Annotations personalizadas
- Eclipse ou outra IDE Java

O banco local utilizado pelo projeto e `banco_rpg.db`. O driver JDBC esta versionado em `lib/sqlite-jdbc-3.53.4.0.jar`.

## Estrutura do projeto

- `src/Main.java`: ponto de entrada e exemplo de uso do DAO.
- `src/Model/GenericDAO.java`: DAO generico responsavel pelo acesso ao banco.
- `src/Model/Personagem.java`: classe abstrata base dos personagens.
- `src/Model/Guerreiro.java`, `src/Model/Mago.java` e `src/Model/Cacador.java`: entidades de personagens.
- `src/Model/Item.java` e suas subclasses: entidades relacionadas aos itens do jogo.
- `src/Model/Table.java`: annotation para configurar o nome da tabela.
- `src/Model/Column.java`: annotation para configurar nome, tamanho e restricoes da coluna.
- `src/Model/Id.java`: annotation para identificar a chave primaria e configurar autoincremento.
- `src/Model/Transient.java`: annotation para ignorar campos na persistencia.

## GenericDAO

O `GenericDAO` identifica a classe, os campos declarados e os campos herdados por Reflection. Os nomes padrao sao derivados do nome da classe e dos atributos, mas podem ser substituidos pelas annotations.

As operacoes disponiveis sao:

- `inserirObjeto(Object objeto)`: insere uma entidade.
- `atualizarObjeto(Object objeto)`: atualiza todos os campos persistiveis usando o ID da entidade.
- `atualizarCampo(Class<T> tipo, Object id, String nomeCampo, Object valor)`: atualiza um campo especifico pelo ID.
- `selecionarTodosObjeto(Class<T> tipo)`: retorna todos os registros como objetos tipados.
- `selecionarUmObjeto(Class<T> tipo, Object id)`: retorna um registro pelo ID.
- `removerObjeto(Class<T> tipo, Object id)`: remove um registro pelo ID.
- `criarTabela(Object objeto)`: gera e executa o `CREATE TABLE` a partir dos metadados da classe.

Os valores sao enviados ao SQLite por `PreparedStatement`. Nomes de tabelas e colunas sao validados antes de serem inseridos nos comandos SQL.

## Annotations de mapeamento

Exemplo de uma entidade configurada:

```java
@Table(name = "clientes")
public class Cliente {
    @Id
    @Column(name = "id_cliente")
    private Integer id;

    @Column(name = "nome_cliente", length = 100, nullable = false, unique = true)
    private String nome;
}
```

Com essa configuracao, `criarTabela(new Cliente())` gera uma tabela chamada `clientes`, com as colunas `id_cliente` e `nome_cliente`.

### Regras das annotations

- Sem `@Table`, o nome da tabela sera o nome simples da classe em letras minusculas.
- Sem `@Column`, o nome da coluna sera o nome do atributo.
- Um campo chamado `id` e tratado como chave primaria mesmo sem `@Id`.
- `@Id(autoIncrement = false)` desativa o autoincremento para aquele identificador.
- `@Column(nullable = false)` adiciona `NOT NULL`.
- `@Column(unique = true)` adiciona `UNIQUE`.
- `@Column(length = 100)` define o tamanho de uma coluna `VARCHAR`.
- `@Column(sqlType = "TEXT")` permite definir diretamente o tipo SQL.
- `@Transient` impede que o atributo seja incluido na tabela, no `INSERT`, no `UPDATE` ou no mapeamento do resultado.

## Mapeamento de tipos

| Tipo Java | Tipo SQL |
| --- | --- |
| `int`, `Integer`, `short`, `Short` | `INTEGER` |
| `long`, `Long` | `BIGINT` |
| `double`, `Double` | `DOUBLE` |
| `float`, `Float` | `FLOAT` |
| `boolean`, `Boolean` | `BOOLEAN` |
| `String` | `VARCHAR` |
| `LocalDate` | `DATE` |
| `LocalDateTime` | `TIMESTAMP` |

Campos estaticos, sinteticos, anotados com `@Transient` e tipos nao suportados nao sao persistidos. Na hierarquia atual, `itens` e `mirando` sao estados temporarios e estao marcados com `@Transient`.

## Execucao

### Pela IDE

1. Importe o projeto como um projeto Java.
2. Verifique se `lib/sqlite-jdbc-3.53.4.0.jar` esta no Build Path.
3. Execute `src/Main.java`.

O `Main` cria por Reflection as tabelas de `Guerreiro`, `Mago` e `Cacador`, aplica uma arma e poções aos personagens, imprime seus atributos atualizados, insere um registro de cada personagem e mostra os dados salvos.

### Pelo terminal

```bash
rm -rf bin/Model bin/Main.class banco_rpg.db
javac --release 8 -Xlint:-options -cp lib/sqlite-jdbc-3.53.4.0.jar -d bin src/Model/*.java src/Main.java
java --enable-native-access=ALL-UNNAMED -cp bin:lib/sqlite-jdbc-3.53.4.0.jar Main
```

O arquivo `banco_rpg.db` e criado automaticamente no diretorio raiz. A saida esperada mostra Aragorn com forca 32, Gandalf com mana 50 e Robin com vida 115 e forca 23, alem de um registro nas tabelas `guerreiro`, `mago` e `cacador`.

O parametro `--enable-native-access=ALL-UNNAMED` evita o aviso do driver SQLite em JDKs mais novos. Em Java 8, execute o mesmo comando sem esse parametro.

Para executar novamente sem acumular registros do teste anterior, remova o arquivo `banco_rpg.db` antes de iniciar o programa.

## Observacao sobre consultas tipadas

`selecionarTodosObjeto(Class<T>)` e `selecionarUmObjeto(Class<T>, Object)` criam instancias por Reflection. Para utiliza-los, a entidade deve possuir um construtor sem argumentos, que pode ser privado. Os metodos que recebem um objeto e imprimem os dados diretamente nao possuem essa exigencia.