package pokesal.batalha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pokesal.excecao.LimiteItensExcedidoException;
import pokesal.modelo.Pokesal;
import pokesal.modelo.Terreno;
import pokesal.modelo.TipoElemental;
import pokesal.modelo.TipoItem;
import pokesal.modelo.Treinador;
import org.junit.jupiter.api.Test;

/**
 * Testes obrigatórios da Fase 02 do torneio Pokésal.
 */
class RegrasObrigatoriasTest {

  @Test
  void testVantagemElemental() {
    TabelaVantagem tabela = new TabelaVantagem();

    assertEquals(ConstantesBatalha.SUPER_EFETIVO,
        tabela.obterMultiplicador(TipoElemental.FOGO, TipoElemental.PLANTA));
    assertEquals(ConstantesBatalha.POUCO_EFETIVO,
        tabela.obterMultiplicador(TipoElemental.FOGO, TipoElemental.AGUA));
    assertEquals(ConstantesBatalha.NEUTRO,
        tabela.obterMultiplicador(TipoElemental.FOGO, TipoElemental.FOGO));

    assertEquals(ConstantesBatalha.SUPER_EFETIVO,
        tabela.obterMultiplicador(TipoElemental.AGUA, TipoElemental.FOGO));
    assertEquals(ConstantesBatalha.POUCO_EFETIVO,
        tabela.obterMultiplicador(TipoElemental.AGUA, TipoElemental.PLANTA));
    assertEquals(ConstantesBatalha.NEUTRO,
        tabela.obterMultiplicador(TipoElemental.AGUA, TipoElemental.AGUA));

    assertEquals(ConstantesBatalha.SUPER_EFETIVO,
        tabela.obterMultiplicador(TipoElemental.PLANTA, TipoElemental.AGUA));
    assertEquals(ConstantesBatalha.POUCO_EFETIVO,
        tabela.obterMultiplicador(TipoElemental.PLANTA, TipoElemental.FOGO));
    assertEquals(ConstantesBatalha.NEUTRO,
        tabela.obterMultiplicador(TipoElemental.PLANTA, TipoElemental.PLANTA));
  }

  @Test
  void testEfeitoTerrenoEstacionamentoUCSal() {
    CalculadoraDano calc = new CalculadoraDano();
    Pokesal charSal = CatalogoInicial.criar("CharSal");
    Pokesal bulbaSal = CatalogoInicial.criar("BulbaSal");

    int danoCanteiro = calc.calcular(charSal, bulbaSal,
        Terreno.CANTEIRO_CENTRAL);
    int danoAsfalto = calc.calcular(charSal, bulbaSal, Terreno.ASFALTO_QUENTE);
    assertTrue(danoAsfalto > danoCanteiro,
        "Fogo no asfalto quente precisa causar mais dano.");

    Pokesal squirtSal = CatalogoInicial.criar("SquirtSal");
    Pokesal cyndaSal = CatalogoInicial.criar("CyndaSal");
    int aguaAsfalto = calc.calcular(squirtSal, cyndaSal,
        Terreno.ASFALTO_QUENTE);
    int aguaPoca = calc.calcular(squirtSal, cyndaSal, Terreno.POCA_CHUVA);
    assertTrue(aguaPoca > aguaAsfalto,
        "Agua na poca de chuva precisa causar mais dano.");

    Treinador planta = new Treinador("Ana", CatalogoInicial.criar("BulbaSal"));
    Treinador fogo = new Treinador("Beto", CatalogoInicial.criar("CharSal"));
    planta.getPokesal().receberDano(20);
    int hpAntes = planta.getPokesal().getHpAtual();
    Batalha batalha = new Batalha(planta, fogo, Terreno.CANTEIRO_CENTRAL);
    batalha.finalizarTurno();
    assertTrue(planta.getPokesal().getHpAtual() > hpAntes,
        "Planta no canteiro precisa recuperar HP no fim do turno.");
    assertEquals(fogo.getPokesal().getHpMaximo(), fogo.getPokesal().getHpAtual(),
        "Fogo no canteiro nao recupera HP.");
  }

  @Test
  void testOrdemDeAtaquePorVelocidade() {
    Treinador rapido = new Treinador("Ana", CatalogoInicial.criar("CharSal"));
    Treinador lento = new Treinador("Beto", CatalogoInicial.criar("SquirtSal"));
    Batalha batalha = new Batalha(rapido, lento, Terreno.ASFALTO_QUENTE);

    assertEquals(rapido, batalha.obterPrimeiroAAgir());
    assertEquals("CharSal",
        batalha.obterOrdemDeAtaque()[0].getPokesal().getNome());
    assertEquals("SquirtSal",
        batalha.obterOrdemDeAtaque()[1].getPokesal().getNome());
  }

  @Test
  void testEmpateSpdDesempatePorAtaque() {
    Pokesal fraco = new Pokesal("Fraco", TipoElemental.FOGO, 40, 30, 40, 50);
    Pokesal forte = new Pokesal("Forte", TipoElemental.AGUA, 40, 70, 40, 50);
    Treinador ana = new Treinador("Ana", fraco);
    Treinador beto = new Treinador("Beto", forte);
    Batalha batalha = new Batalha(ana, beto, Terreno.ASFALTO_QUENTE);

    assertEquals(50, fraco.getSpdEfetivo());
    assertEquals(50, forte.getSpdEfetivo());
    assertEquals(beto, batalha.obterPrimeiroAAgir());

    Pokesal iguaisA = new Pokesal("A", TipoElemental.FOGO, 40, 50, 40, 50);
    Pokesal iguaisB = new Pokesal("B", TipoElemental.AGUA, 40, 50, 40, 50);
    Treinador t1 = new Treinador("Primeiro", iguaisA);
    Treinador t2 = new Treinador("Segundo", iguaisB);
    Batalha empateTotal = new Batalha(t1, t2, Terreno.POCA_CHUVA);
    assertEquals(t1, empateTotal.obterPrimeiroAAgir());
  }

  @Test
  void testUsoLimiteDeItensExcedido() {
    Treinador treinador = new Treinador("Ana",
        CatalogoInicial.criar("CharSal"));
    treinador.getPokesal().receberDano(30);

    treinador.usarItem(TipoItem.POTION);
    treinador.usarItem(TipoItem.POTION);
    assertEquals(ConstantesBatalha.LIMITE_ITENS_POR_BATALHA,
        treinador.getItensUsados());

    assertThrows(LimiteItensExcedidoException.class, () -> {
      treinador.usarItem(TipoItem.ANTIDOTE);
    });
    assertEquals(ConstantesBatalha.LIMITE_ITENS_POR_BATALHA,
        treinador.getItensUsados());
  }

  @Test
  void testCalculoDanoBoundaryValues() {
    CalculadoraDano calc = new CalculadoraDano();
    Pokesal atkMinimo = new Pokesal("Fraco", TipoElemental.FOGO, 10, 1, 1, 1);
    Pokesal defAlta = new Pokesal("Tanque", TipoElemental.FOGO, 200, 1, 100, 1);

    int danoMinimo = calc.calcular(atkMinimo, defAlta, Terreno.CANTEIRO_CENTRAL);
    assertEquals(ConstantesBatalha.DANO_MINIMO, danoMinimo);

    defAlta.receberDano(500);
    assertEquals(ConstantesBatalha.HP_MINIMO, defAlta.getHpAtual());

    defAlta.curar(40);
    assertEquals(40, defAlta.getHpAtual());
    defAlta.curar(1000);
    assertEquals(200, defAlta.getHpAtual());

    Pokesal atkAlto = new Pokesal("Forte", TipoElemental.FOGO, 50, 200, 10, 10);
    Pokesal defMinima = new Pokesal("Vidro", TipoElemental.PLANTA, 30, 10, 1, 10);
    int danoAlto = calc.calcular(atkAlto, defMinima, Terreno.ASFALTO_QUENTE);
    assertTrue(danoAlto > ConstantesBatalha.DANO_MINIMO);
    defMinima.receberDano(danoAlto);
    assertTrue(defMinima.getHpAtual() >= ConstantesBatalha.HP_MINIMO);
  }
}
