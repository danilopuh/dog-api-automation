# language: pt
@dogapi
Funcionalidade: Consulta de raças de cachorros
  Como um usuário da Dog API
  Eu quero consultar informações sobre raças de cachorros
  Para obter dados precisos e atualizados

  Contexto:
    Dado que a Dog API está disponível

  @smoke @racas
  Cenário: Listar todas as raças disponíveis
    Quando eu consulto a lista de todas as raças
    Então a resposta deve ter status 200
    E o tempo de resposta deve ser menor que 2 segundos
    E a resposta deve conter o status "success"
    E a lista de raças não deve estar vazia
    E a raça "hound" deve estar presente na lista
    E a resposta deve seguir o schema JSON de lista de raças

  @imagens @racas
  Cenário: Consultar imagens de uma raça específica
    Dado que existe a raça "hound"
    Quando eu consulto as imagens da raça "hound"
    Então a resposta deve ter status 200
    E o tempo de resposta deve ser menor que 3 segundos
    E a resposta deve conter o status "success"
    E deve retornar pelo menos 1 imagem
    E todas as URLs devem ser válidas (começar com http)
    E a resposta deve seguir o schema JSON de imagens de raça

  @imagens @aleatorio
  Cenário: Obter imagem aleatória de cachorro
    Quando eu solicito uma imagem aleatória
    Então a resposta deve ter status 200
    E o tempo de resposta deve ser menor que 2 segundos
    E a resposta deve conter o status "success"
    E a URL da imagem deve ser válida
    E a URL deve ter uma extensão de imagem válida
    E a resposta deve seguir o schema JSON de imagem aleatória

  @negativo @racas
  Esquema do Cenário: Validar erro para raças inexistentes
    Quando eu consulto as imagens da raça "<raca_inexistente>"
    Então a resposta deve indicar erro para raça inexistente

    Exemplos:
      | raca_inexistente     |
      | raca-inexistente-xyz |
      | unicornio           |
      | dragao              |

  @performance @contratos
  Cenário: Verificar performance e contratos básicos
    Quando eu consulto a lista de todas as raças
    Então a resposta deve ter status 200
    E o tempo de resposta deve ser menor que 2.5 segundos
    E a resposta deve conter o status "success"
    E a mensagem não deve ser nula