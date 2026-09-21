package pokesal;

import pokesal.batalha.Batalha;
import pokesal.batalha.CatalogoInicial;
import pokesal.modelo.AcaoTurno;
import pokesal.modelo.Pokesal;
import pokesal.modelo.Terreno;
import pokesal.modelo.TipoItem;
import pokesal.modelo.Treinador;
import java.util.Scanner;

/**
 * Entrada do sistema: batalha no console entre dois treinadores.
 */
public final class Main {

  private Main() {
  }

  /**
   * Inicia o torneio no console.
   *
   * @param args não utilizados
   */
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    System.out.println("==============================================");
    System.out.println("  Torneio Pokésal - UCSal Pituaçu");
    System.out.println("  Estacionamento oficial de batalhas");
    System.out.println("==============================================");
    System.out.println();

    Treinador treinador1 = lerTreinador(scanner, 1);
    Treinador treinador2 = lerTreinador(scanner, 2);
    Terreno terreno = lerTerreno(scanner);

    Batalha batalha = new Batalha(treinador1, treinador2, terreno);
    System.out.println();
    System.out.println("Batalha no terreno " + nomeTerreno(terreno) + "!");
    System.out.println(treinador1.getNome() + " ("
        + descricaoPokesal(treinador1.getPokesal()) + ") vs "
        + treinador2.getNome() + " ("
        + descricaoPokesal(treinador2.getPokesal()) + ")");
    System.out.println();

    while (!batalha.batalhaEncerrada()) {
      imprimirEstado(batalha);
      AcaoEscolhida escolha1 = lerAcaoValida(scanner, treinador1);
      AcaoEscolhida escolha2 = lerAcaoValida(scanner, treinador2);
      batalha.resolverTurno(escolha1.acao, escolha1.item, escolha2.acao,
          escolha2.item);
      String relatorio = batalha.consumirRelatorio();
      if (!relatorio.isEmpty()) {
        System.out.println();
        System.out.print(relatorio);
      }
      System.out.println();
    }

    Treinador vencedor = batalha.obterVencedor();
    System.out.println("Vencedor: " + vencedor.getNome() + " com "
        + vencedor.getPokesal().getNome() + "!");
    scanner.close();
  }

  private static Treinador lerTreinador(Scanner scanner, int numero) {
    System.out.print("Nome do treinador " + numero + ": ");
    String nome = scanner.nextLine().trim();
    while (nome.isEmpty()) {
      System.out.print("Digite um nome: ");
      nome = scanner.nextLine().trim();
    }
    String nomeInicial = lerNomeInicial(scanner, nome);
    Pokesal inicial = CatalogoInicial.criar(nomeInicial);
    return new Treinador(nome, inicial);
  }

  private static String lerNomeInicial(Scanner scanner, String nomeTreinador) {
    String[] nomes = CatalogoInicial.nomesPermitidos();
    System.out.println(nomeTreinador + ", escolha seu inicial:");
    for (int i = 0; i < nomes.length; i++) {
      Pokesal preview = CatalogoInicial.criar(nomes[i]);
      System.out.println("  " + (i + 1) + ") " + preview.getNome()
          + "  tipo=" + preview.getTipo()
          + "  HP=" + preview.getHpMaximo()
          + " ATK=" + preview.getAtkBase()
          + " DEF=" + preview.getDefBase()
          + " SPD=" + preview.getSpdBase());
    }
    int opcao = lerOpcao(scanner, 1, nomes.length);
    return nomes[opcao - 1];
  }

  private static Terreno lerTerreno(Scanner scanner) {
    System.out.println("Escolha o terreno do estacionamento:");
    System.out.println("  1) Asfalto Quente (Dia) - Fogo +15% de dano");
    System.out.println("  2) Poca de Chuva - Agua +10% de dano");
    System.out.println("  3) Canteiro Central - Planta recupera 5% do HP");
    int opcao = lerOpcao(scanner, 1, 3);
    if (opcao == 1) {
      return Terreno.ASFALTO_QUENTE;
    }
    if (opcao == 2) {
      return Terreno.POCA_CHUVA;
    }
    return Terreno.CANTEIRO_CENTRAL;
  }

  private static AcaoEscolhida lerAcaoValida(Scanner scanner,
      Treinador treinador) {
    while (true) {
      System.out.println(treinador.getNome() + ", o que "
          + treinador.getPokesal().getNome() + " vai fazer?");
      System.out.println("  1) Golpe elemental (tipo do Pokesal)");
      System.out.println("  2) Investida (golpe neutro)");
      System.out.println("  3) Usar item (restam "
          + treinador.getItensRestantes() + ")");
      int opcao = lerOpcao(scanner, 1, 3);
      if (opcao == 1) {
        return new AcaoEscolhida(AcaoTurno.ATACAR, null);
      }
      if (opcao == 2) {
        return new AcaoEscolhida(AcaoTurno.INVESTIDA, null);
      }
      TipoItem item = lerItem(scanner);
      if (treinador.getItensRestantes() <= 0) {
        System.out.println(treinador.getNome()
            + " ja usou o limite de itens nesta batalha.");
        System.out.println("Escolhe outra acao.");
      } else {
        return new AcaoEscolhida(AcaoTurno.USAR_ITEM, item);
      }
    }
  }

  private static TipoItem lerItem(Scanner scanner) {
    System.out.println("Qual item?");
    System.out.println("  1) Potion (+20 HP)");
    System.out.println("  2) Super Potion (+50 HP)");
    System.out.println("  3) Antidote (remove status)");
    int opcao = lerOpcao(scanner, 1, 3);
    if (opcao == 1) {
      return TipoItem.POTION;
    }
    if (opcao == 2) {
      return TipoItem.SUPER_POTION;
    }
    return TipoItem.ANTIDOTE;
  }

  private static void imprimirEstado(Batalha batalha) {
    System.out.println("---------- Turno " + batalha.getNumeroTurno()
        + " ----------");
    imprimirPokesal(batalha.getTreinador1());
    imprimirPokesal(batalha.getTreinador2());
    Treinador primeiro = batalha.obterPrimeiroAAgir();
    System.out.println("Iniciativa: " + primeiro.getPokesal().getNome()
        + " (SPD " + primeiro.getPokesal().getSpdEfetivo()
        + ", ATK " + primeiro.getPokesal().getAtkEfetivo() + ")");
  }

  private static void imprimirPokesal(Treinador treinador) {
    Pokesal p = treinador.getPokesal();
    System.out.println(treinador.getNome() + " - " + descricaoPokesal(p)
        + "  HP " + p.getHpAtual() + "/" + p.getHpMaximo()
        + "  status=" + p.getStatus());
  }

  private static String descricaoPokesal(Pokesal pokesal) {
    return pokesal.getNome() + " [" + pokesal.getTipo() + "]";
  }

  private static String nomeTerreno(Terreno terreno) {
    if (terreno == Terreno.ASFALTO_QUENTE) {
      return "Asfalto Quente";
    }
    if (terreno == Terreno.POCA_CHUVA) {
      return "Poca de Chuva";
    }
    return "Canteiro Central";
  }

  private static int lerOpcao(Scanner scanner, int min, int max) {
    while (true) {
      System.out.print("Opcao: ");
      String linha = scanner.nextLine().trim();
      try {
        int valor = Integer.parseInt(linha);
        if (valor >= min && valor <= max) {
          return valor;
        }
      } catch (NumberFormatException e) {
        // volta a pedir
      }
      System.out.println("Opcao invalida. Digite um numero de " + min
          + " a " + max + ".");
    }
  }

  private static final class AcaoEscolhida {
    private final AcaoTurno acao;
    private final TipoItem item;

    private AcaoEscolhida(AcaoTurno acao, TipoItem item) {
      this.acao = acao;
      this.item = item;
    }
  }
}
