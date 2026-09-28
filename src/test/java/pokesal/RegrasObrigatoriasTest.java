package pokesal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import pokesal.batalha.Batalha;
import pokesal.batalha.CalculadoraDano;
import pokesal.batalha.CatalogoInicial;
import pokesal.batalha.ConstantesBatalha;
import pokesal.batalha.TabelaVantagem;
import pokesal.excecao.LimiteItensExcedidoException;
import pokesal.modelo.Pokesal;
import pokesal.modelo.Terreno;
import pokesal.modelo.TipoElemental;
import pokesal.modelo.TipoItem;
import pokesal.modelo.Treinador;

/**
 * Testes obrigatórios da Fase 02 (nomes pedidos no enunciado).
 */
class RegrasObrigatoriasTest {

  @Test
  void testVantagemElemental() {
    TabelaVantagem tabela = new TabelaVantagem();
    assertEquals(ConstantesBatalha.SUPER_EFETIVO,
        tabela.obterMultiplicador(TipoElemental.FOGO, TipoElemental.PLANTA));
    assertEquals(ConstantesBatalha.POUCO_EFETIVO,
        tabela.obterMultiplicador(TipoElemental.FOGO, TipoElemental.AGUA));
    assertEquals(ConstantesBatalha.SUPER_EFETIVO,
        tabela.obterMultiplicador(TipoElemental.AGUA, TipoElemental.FOGO));
    assertEquals(ConstantesBatalha.POUCO_EFETIVO,
        tabela.obterMultiplicador(TipoElemental.AGUA, TipoElemental.PLANTA));
    assertEquals(ConstantesBatalha.SUPER_EFETIVO,
        tabela.obterMultiplicador(TipoElemental.PLANTA, TipoElemental.AGUA));
    assertEquals(ConstantesBatalha.POUCO_EFETIVO,
        tabela.obterMultiplicador(TipoElemental.PLANTA, TipoElemental.FOGO));
    assertEquals(ConstantesBatalha.NEUTRO,
        tabela.obterMultiplicador(TipoElemental.FOGO, TipoElemental.FOGO));

    CalculadoraDano calc = new CalculadoraDano();
    Pokesal charSal = CatalogoInicial.criar("CharSal");
    Pokesal bulbaSal = CatalogoInicial.criar("BulbaSal");
    Pokesal squirtSal = CatalogoInicial.criar("SquirtSal");
    int superEfetivo = calc.calcular(charSal, bulbaSal, Terreno.POCA_CHUVA);
    int poucoEfetivo = calc.calcular(charSal, squirtSal, Terreno.POCA_CHUVA);
    int neutro = calc.calcular(charSal, CatalogoInicial.criar("CyndaSal"),
        Terreno.POCA_CHUVA);
    assertTrue(superEfetivo > neutro);
    assertTrue(neutro > poucoEfetivo);
  }

  @Test
  void testEfeitoTerrenoEstacionamentoUCSal() {
    CalculadoraDano calc = new CalculadoraDano();
    Pokesal charSal = CatalogoInicial.criar("CharSal");
    Pokesal bulbaSal = CatalogoInicial.criar("BulbaSal");
    Pokesal squirtSal = CatalogoInicial.criar("SquirtSal");

    int fogoSemBonus = calc.calcular(charSal, bulbaSal, Terreno.POCA_CHUVA);
    int fogoNoAsfalto = calc.calcular(charSal, bulbaSal,
        Terreno.ASFALTO_QUENTE);
    assertTrue(fogoNoAsfalto > fogoSemBonus);
    assertEquals(1.15, calc.obterMultiplicadorTerreno(TipoElemental.FOGO,
        Terreno.ASFALTO_QUENTE));

    int aguaSemBonus = calc.calcular(squirtSal, charSal,
        Terreno.ASFALTO_QUENTE);
    int aguaNaPoca = calc.calcular(squirtSal, charSal, Terreno.POCA_CHUVA);
    assertTrue(aguaNaPoca > aguaSemBonus);
    assertEquals(1.10, calc.obterMultiplicadorTerreno(TipoElemental.AGUA,
        Terreno.POCA_CHUVA));

    Treinador t1 = new Treinador("A", CatalogoInicial.criar("BulbaSal"));
    Treinador t2 = new Treinador("B", CatalogoInicial.criar("CharSal"));
    Batalha batalha = new Batalha(t1, t2, Terreno.CANTEIRO_CENTRAL);
    t1.getPokesal().receberDano(10);
    int hpAntes = t1.getPokesal().getHpAtual();
    batalha.finalizarTurno();
    assertTrue(t1.getPokesal().getHpAtual() > hpAntes);
  }

  @Test
  void testOrdemDeAtaquePorVelocidade() {
    Treinador rapido = new Treinador("Miguel", CatalogoInicial.criar("CharSal"));
    Treinador lento = new Treinador("Dantton", CatalogoInicial.criar("BulbaSal"));
    Batalha batalha = new Batalha(rapido, lento, Terreno.POCA_CHUVA);
    Treinador[] ordem = batalha.obterOrdemDeAtaque();
    assertEquals(rapido, ordem[0]);
    assertEquals(lento, ordem[1]);
    assertEquals(rapido, batalha.obterPrimeiroAAgir());
    assertTrue(rapido.getPokesal().getSpdEfetivo()
        > lento.getPokesal().getSpdEfetivo());
  }

  @Test
  void testUsoLimiteDeItensExcedido() {
    Treinador treinador = new Treinador("Dantton",
        CatalogoInicial.criar("CharSal"));
    treinador.getPokesal().receberDano(30);
    treinador.usarItem(TipoItem.POTION);
    treinador.usarItem(TipoItem.POTION);
    assertEquals(2, treinador.getItensUsados());
    assertEquals(0, treinador.getItensRestantes());
    assertThrows(LimiteItensExcedidoException.class,
        () -> treinador.usarItem(TipoItem.ANTIDOTE));
  }

  @Test
  void testCalculoDanoBoundaryValues() {
    CalculadoraDano calc = new CalculadoraDano();
    Pokesal fraco = new Pokesal("Fraco", TipoElemental.FOGO, 1, 1, 1, 1);
    Pokesal muro = new Pokesal("Muro", TipoElemental.FOGO, 200, 1, 200, 1);
    int danoMinimo = calc.calcular(fraco, muro, Terreno.POCA_CHUVA);
    assertEquals(ConstantesBatalha.DANO_MINIMO, danoMinimo);

    Pokesal alvo = CatalogoInicial.criar("BulbaSal");
    int hpCheio = alvo.getHpAtual();
    alvo.receberDano(9999);
    assertEquals(0, alvo.getHpAtual());
    assertTrue(alvo.estaNocauteado());
    alvo.curar(9999);
    assertEquals(hpCheio, alvo.getHpAtual());

    Pokesal atkAlto = new Pokesal("Forte", TipoElemental.FOGO, 50, 200, 10,
        10);
    Pokesal defBaixa = new Pokesal("Frágil", TipoElemental.PLANTA, 50, 10, 1,
        10);
    int danoAlto = calc.calcular(atkAlto, defBaixa, Terreno.POCA_CHUVA);
    assertTrue(danoAlto >= ConstantesBatalha.DANO_MINIMO);
    defBaixa.receberDano(danoAlto);
    assertTrue(defBaixa.getHpAtual() >= ConstantesBatalha.HP_MINIMO);
  }

  @Test
  void testEmpateSpdDesempatePorAtaque() {
    Pokesal maisForte = new Pokesal("Forte", TipoElemental.FOGO, 40, 60, 40,
        50);
    Pokesal maisFraco = new Pokesal("Fraco", TipoElemental.AGUA, 40, 30, 40,
        50);
    Treinador t1 = new Treinador("Miguel", maisFraco);
    Treinador t2 = new Treinador("Dantton", maisForte);
    Batalha batalha = new Batalha(t1, t2, Terreno.POCA_CHUVA);
    assertEquals(t2, batalha.obterPrimeiroAAgir());
  }
}
