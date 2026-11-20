# Sistema de Biblioteca

Projeto da aula em `jesus`, com Livro, Autor, Categoria, Emprestimo e Usuario.

## Executar no PowerShell

O projeto mantém as versões originais e exige JDK 25. Dentro da pasta `jesus`:

```powershell
.\mvnw.cmd spring-boot:run
```

Partindo da raiz deste repositório, use `cd .\jesus` antes do comando.
A página inicial está em http://localhost:8080/livros/inicio.
Use Ctrl+C para encerrar.

Para compilar e executar os testes:

```powershell
.\mvnw.cmd compile test
```

## Implementação

As cinco controllers seguem o padrão da aula: uma `List<Entidade>` inicializada com
`new ArrayList<>()` como atributo da controller, `listar`, `cadastrar`, `editar` e
`excluir`. As operações usam `add`, `set` e `remove`. Edição e exclusão verificam
somente `indice >= 0 && indice < lista.size()`; índices fora dos limites não alteram
a lista e também redirecionam para a listagem.

Foram removidos os IDs acrescentados às models, os contadores, as buscas por ID,
as validações e o binding de formulários da implementação anterior. Foram
substituídos os templates de listagem e removidos os formulários, detalhes,
fragmento compartilhado e mensagens de validação daquela implementação.
Os testes anteriores foram ajustados para o CRUD com JSON e índices.

Os atributos originais das models foram preservados. Não há banco de dados,
services, repositories, DTOs ou tratamento global. O POM não foi alterado.

Cada entidade tem apenas `listar.html`, mostrando o índice e os atributos,
sem exibir senhas. `templates/livros/inicio.html` foi preservado e tem links para
as cinco listagens. Não há formulários nem JavaScript.

## Testar pelo Postman

Base: `http://localhost:8080`.

| Entidade | Cadastrar (POST) | Listar (GET) | Editar (PUT) | Excluir (DELETE) |
| --- | --- | --- | --- | --- |
| Livro | `/livros/cadastrar` | `/livros` | `/livros/editar/0` | `/livros/excluir/0` |
| Autor | `/autores/cadastrar` | `/autores` | `/autores/editar/0` | `/autores/excluir/0` |
| Categoria | `/categorias/cadastrar` | `/categorias` | `/categorias/editar/0` | `/categorias/excluir/0` |
| Emprestimo | `/emprestimos/cadastrar` | `/emprestimos` | `/emprestimos/editar/0` | `/emprestimos/excluir/0` |
| Usuario | `/usuarios/cadastrar` | `/usuarios` | `/usuarios/editar/0` | `/usuarios/excluir/0` |

Em POST e PUT, selecione **Body > raw > JSON** e envie `Content-Type: application/json`.
GET e DELETE não precisam de corpo. A listagem retorna HTML.
POST, PUT e DELETE retornam redirecionamento HTTP 302 para a listagem.
Para visualizar esse retorno diretamente, desative o acompanhamento automático
de redirecionamentos no Postman.

PUT substitui o registro inteiro. Envie todos os atributos, incluindo a senha
ao editar Usuario.

### Livro

POST `http://localhost:8080/livros/cadastrar`:

```json
{
  "titulo": "Dom Casmurro",
  "autor": "Machado de Assis",
  "isbn": "9788535914849"
}
```

PUT `http://localhost:8080/livros/editar/0`:

```json
{
  "titulo": "Memorias Postumas",
  "autor": "Machado de Assis",
  "isbn": "9788535914856"
}
```

### Autor

POST `http://localhost:8080/autores/cadastrar`:

```json
{
  "nome": "Machado de Assis",
  "nacionalidade": "Brasileira",
  "obrasPublicadas": 10
}
```

PUT `http://localhost:8080/autores/editar/0`:

```json
{
  "nome": "Raphael Montes",
  "nacionalidade": "Brasileira",
  "obrasPublicadas": 12
}
```

### Categoria

POST `http://localhost:8080/categorias/cadastrar`:

```json
{
  "genero": "Terror"
}
```

PUT `http://localhost:8080/categorias/editar/0`:

```json
{
  "genero": "Romance"
}
```

### Emprestimo

POST `http://localhost:8080/emprestimos/cadastrar`:

```json
{
  "statusEmprestimo": "Emprestado",
  "dataEmprestimo": "20/11/2026"
}
```

PUT `http://localhost:8080/emprestimos/editar/0`:

```json
{
  "statusEmprestimo": "Devolvido",
  "dataEmprestimo": "21/11/2026"
}
```

### Usuario

POST `http://localhost:8080/usuarios/cadastrar`:

```json
{
  "nome": "Sofia",
  "cpf": "11122233344",
  "usuario": "sofia123",
  "senha": "SegredoInicial123"
}
```

PUT `http://localhost:8080/usuarios/editar/0`:

```json
{
  "nome": "Sofia Silva",
  "cpf": "55566677788",
  "usuario": "sofia.silva",
  "senha": "NovoSegredo456"
}
```

Os índices começam em **zero**. Depois de excluir um registro, os seguintes
mudam de posição: ao remover o índice 0, o antigo índice 1 passa a ser 0.
Consulte a listagem antes de editar ou excluir.

Os dados ficam somente na memória e **desaparecem ao reiniciar a aplicação**.

## Verificação realizada

` .\mvnw.cmd compile test` terminou com **BUILD SUCCESS**: 7 testes executados,
sem falhas, erros ou testes ignorados. Inclui o teste de contexto original,
os cinco fluxos de CRUD com JSON e índices e os links da página inicial.
