# Pokésal

Sistema de batalha do torneio no estacionamento da UCSal Pituaçu.

A regra do jogo é simples: cada treinador entra com **um** inicial, o terreno do estacionamento interfere na luta, e a batalha roda em turnos até um dos dois cair. Não empata e não tem teto de turno. Quem tiver mais SPD ataca primeiro. Mochila vale, mas só dois itens por luta.

O código é Java com POO bem direta (classe, encapsulamento, enum, exceção). Dá pra jogar no console e os testes JUnit batem nas regras que o trabalho pede.

## Como rodar no Eclipse

1. **File > Import > Maven > Existing Maven Projects**
2. Seleciona esta pasta (`pokesal`)
3. Botão direito em `src/main/java/pokesal/Main.java` → **Run As > Java Application**

No terminal:

```
mvn test
```

## Iniciais

Só entra quem está na lista. Qualquer outro nome estoura `InicialInvalidoException`.

| Nome      | Tipo   | HP | ATK | DEF | SPD |
|-----------|--------|----|-----|-----|-----|
| BulbaSal  | Planta | 45 | 49  | 49  | 45  |
| CharSal   | Fogo   | 39 | 52  | 43  | 65  |
| SquirtSal | Água   | 44 | 48  | 65  | 43  |
| ChikoSal  | Planta | 50 | 45  | 55  | 40  |
| CyndaSal  | Fogo   | 40 | 55  | 40  | 60  |
| TotoSal   | Água   | 50 | 50  | 48  | 43  |

## Regras que estão no código

**Tipos.** Fogo é super efetivo em Planta (x2) e fraco em Água (x0.5). Água é super efetiva em Fogo e fraca em Planta. Planta é super efetiva em Água e fraca em Fogo. Mesmo tipo fica neutro (x1).

**Terreno.** Asfalto Quente aumenta dano de Fogo em 15%. Poça de Chuva aumenta dano de Água em 10%. Canteiro Central faz Pokésal de Planta recuperar 5% do HP máximo no fim do turno.

O enunciado da poça falava em “precisão ou dano”. A gente foi de **dano**. Precisão aleatória bagunça teste unitário e a defesa oral.

**Turno.** A ordem é SPD. Usar item gasta o turno. No fim: regen do canteiro, depois o tick de status.

O enunciado não diz o que acontece se a SPD empatar. **Correção de requisito (não é autoral):** empate de SPD vai para quem tiver mais ATK efetivo. Se o ATK também empatar, o treinador 1 começa.

**Dois golpes.** Golpe elemental usa o tipo do Pokésal (vantagem + terreno). Investida é neutra.

**Status.** Golpe de Fogo deixa Queimado, Água deixa Paralisado, Planta deixa Envenenado.

- Queimado: ATK cai pela metade e tira 10% do HP máximo por turno
- Envenenado: dano sobe a cada turno (5%, 10%, 15%…)
- Paralisado: SPD cai pela metade (muda a iniciativa no turno seguinte)

**Itens.** No máximo 2 por batalha. O terceiro lança `LimiteItensExcedidoException`.

- Potion: +20 HP
- Super Potion: +50 HP
- Antidote: tira o status

HP nunca fica negativo e cura nunca passa do máximo.

## Fórmula de dano

```
dano = arredonda( (ATK efetivo * vantagem * terreno * 10) / DEF efetiva )
se for combo: dano * 1.5
mínimo = 1
```

Investida ignora vantagem e terreno (multiplicador 1.0).

## Pastas

```
src/main/java/pokesal/
  Main.java                 console
  modelo/                   Pokesal, Treinador, enums
  batalha/                  dano, vantagem, turno, catálogo
  excecao/                  limite de item, inicial inválido
```

## Checkstyle (Google Java Style)

O que a gente tentou seguir no código:

- classe em PascalCase, método/variável em camelCase, constante em UPPER_SNAKE
- Javadoc em classe e método público
- número mágico virou constante em `ConstantesBatalha`
- linha curta (por volta de 100 caracteres) e indentação de 2 espaços
