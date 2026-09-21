# Declaração de uso de IA

Equipe Chaos — PokeSal (Fase 01)

Usamos IAs em duas coisas nesta fase: correção ortográfica / revisão de linguagem do relatório e explicação de erro que o compilador apontava. Decisão de requisito, autoral, diagrama e o código em si ficaram com a equipe.

## 1. Ortografia e linguagem do relatório

**Onde:** `relatorio.tex`

**Para quê:** acento, concordância, frase cortada no meio e o combinado de escrever “de fulano” (não “do fulano” / “da fulana”). Texto mais curto e com cara de aluno de 4º semestre, não de artigo.

**Prompts usados:**

- escrevi um relatório em TEX (anexado). dê uma olhada e faça observações em relação a ortografia e sintaxe do LaTex. estamos usando overleaf web para compilar os pdfs e usando o modelo base da universidade para capa e regras de formatação, não altere-os ou sugira alteração nos mesmos.

**O que a equipe fez depois:** leu o texto, tirou ou corrigiu o que não estava correto no relatório e compilou de novo.

## 2. Erros de compilador

**Onde:** Java (`pokesal.Main`) e LaTeX (`relatorio.tex`)

**Para quê:** traduzir a mensagem de erro e apontar a causa, sem a ferramenta “consertar no automático” sem a gente ver.

**Prompts usados:**

- explique esse erro que aparece quando eu tento executar o projeto. é um problema na versão compilada(build) do programa?  
  (saída: `Error: Could not find or load main class pokesal.Main` / `ClassNotFoundException: pokesal.Main`, classpath em `target/classes`)

**O que a gente viu na prática:**

- Java: `target/classes` imcompleto -> o Java rodava o `.class` de uma pasta que ainda não tinha a build completa. Compilou, o Main subiu.

**Prompt-tipo quando o pdflatex quebrava:**

- o compilador do latex apontou esse erro, explica o que é e como a gente conserta, sem mudar o conteúdo do relatório