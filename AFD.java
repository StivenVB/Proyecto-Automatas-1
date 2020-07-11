/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo.Automata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;
import javax.swing.JOptionPane;

/**
 *
 * @author matiassebastianparra
 */
public class AFD {

    Estado estado;

    public ArrayList<Estado> listaEstados;
    ArrayList<String> alfabeto;
    public ArrayList<String> inicios;
    public ArrayList<String> finales;
    Automata afnd;
    Stack<Estado> pila_estados;
    Estado estado_inicial;
    Estado sumidero;

    public AFD() {

    }

    public AFD(Automata automata, ArrayList<String> alfabeto) {

        this.listaEstados = new ArrayList();
        this.alfabeto = alfabeto;
        this.inicios = new ArrayList();
        this.finales = new ArrayList();
        this.pila_estados = new Stack();

        this.afnd = automata;
        //Se obtienen todos los estados a los que se puede llegar desde el estado 0
        //usando solo transiciones epsilon
        obtener_eclosure();

        //se crea el estado inicial para el afd y un estado sumidero
        estado_inicial = new Estado(0, true, false);
        sumidero = new Estado(1, false, false);

        //se agregan los ciclos correspondientes al sumidero
        for (String string : this.alfabeto) {
            sumidero.agregarTransicion(string, sumidero);
        }

        System.out.println(estado_inicial.estados.addAll(pila_estados));
        pila_estados.clear();

        //se agregan los estados creados prevamente a la lista de estados que representara nuestro AFD
        this.listaEstados.add(estado_inicial);
        this.listaEstados.add(sumidero);

        //se agrega el estado inicial a la pila de la cual obtendremos los estados
        //necesarios para calcular los nuevos estados del AFD
        this.pila_estados.push(estado_inicial);

        while (this.pila_estados.empty() != true) {
            this.generar_estados_afd(this.pila_estados.pop());
        }

        //se modifican los id de cada estado creado, para que 
        //la funcion delta que se imprimira sea mas entendible.
        for (int i = 0; i < listaEstados.size(); i++) {
            listaEstados.get(i).id = i;
        }

    }

    //imprime de manera estandar el AFD
    public String imprimir_afd() {
        String respuesta = "";
        respuesta += "" + "\n";
        respuesta += "AFD" + "\n";
        respuesta += "K = { ";
        for (int i = 0; i < this.listaEstados.size(); i++) {
            if (this.listaEstados.size() - 1 == i) {
                respuesta += "q" + listaEstados.get(i).id;
            } else {
                respuesta += "q" + listaEstados.get(i).id + ",";
            }
        }
        respuesta += " }" + "\n";
        respuesta += "Sigma = [";
        for (int n = 0; n < this.alfabeto.size(); n++) {
            if (this.alfabeto.size() - 1 == n) {
                respuesta += this.alfabeto.get(n) + "]" + "\n";
            } else {
                respuesta += this.alfabeto.get(n) + ", ";
            }
        }
        respuesta += "Delta :" + "\n";
        for (Estado estado : this.listaEstados) {
            respuesta += estado.imprimir_transiciones();
        }
        respuesta += "s = { q" + this.estado_inicial.id + " }" + "\n";
        respuesta += "F = { ";
        for (int i = 0; i < this.listaEstados.size(); i++) {
            if (this.listaEstados.get(i).fin) {
                if (this.listaEstados.size() - 1 == i) {
                    respuesta += "q" + this.listaEstados.get(i).id;

                } else {
                    respuesta += "q" + this.listaEstados.get(i).id + ",";
                }
            }
            System.out.println("q" + this.listaEstados.get(i).id);

        }

        respuesta += " }";
        return respuesta;
    }

    //convierte las transiciones de un estado a los estados y transiciones que 
    //se utilizaran para armar el AFD.
    public void generar_estados_afd(Estado estado) {
        for (String string : this.alfabeto) {
            ArrayList<Estado> transiciones = new ArrayList<Estado>();

            //se obtienen todos los estados a los que accede un 
            //estado especifico usando una letra del alfabeto y
            //transiciones epsilon.
            for (Estado s : estado.estados) {
                if (s.transiciones.containsKey(string.charAt(0)) == true) {
                    for (Estado aux : s.transiciones.get(string.charAt(0))) {
                        obtener_transiciones_estado(aux, transiciones);
                    }
                }
            }

            //si el estado tiene transiciones con el caracter especificado
            //se agrega a la lista de estados del AFD y a la pila para que se generen 
            // nuevos estados y trancisiones a traves de este nuevo estado.
            if (transiciones.isEmpty() == false) {
                Estado nuevo_estado = new Estado(0, false, false);
                nuevo_estado.estados.addAll(transiciones);

                estado.agregarTransicion(Character.toString(string.charAt(0)), nuevo_estado);

                nuevo_estado.verificar_estado_final();

                if (verificar_existencia_estado(nuevo_estado) == false) {
                    this.listaEstados.add(nuevo_estado);
                    this.pila_estados.push(nuevo_estado);
                }

                transiciones.clear();
            } else {
                //si el estado no tiene transiciones con algun caracter del alfabeto
                // entonces se le agregan transiciones a un estado sumidero usando 
                //el caracter especificado.
                estado.agregarTransicion(Character.toString(string.charAt(0)), this.sumidero);
            }
        }
    }

    //obtiene todas las transiciones epsilon de un estado especifico y las almacena en 
    //un arraylist que contiene todos los estados que componen un estado del AFD
    public void obtener_transiciones_estado(Estado estado, ArrayList<Estado> transiciones) {
        if (transiciones.contains(estado) == false) {
            transiciones.add(estado);

            if (estado.transiciones.containsKey('_') == true) {
                for (Estado s : estado.transiciones.get('_')) {
                    obtener_transiciones_estado(s, transiciones);
                }
            }
        }
    }

    //verifica si es que ya se ha analizado un estado previamente y se agrega 
    // a la lista de estados en caso de que no se haya analizado
    public boolean verificar_existencia_estado(Estado estado) {
        for (Estado s : this.listaEstados) {
            if (s.estados.containsAll(estado.estados) == true) {
                return true;
            }
        }

        return false;
    }

    public void obtener_eclosure() {
        transicion_epsilon(afnd.inicio);
    }

    //funcion que obtiene los estados a los que se puede llegar desde el estado 0
    //usando solo transiciones epsilon
    public void transicion_epsilon(Estado estado) {

        if (pila_estados.contains(estado) == false) {
            this.pila_estados.push(estado);

            if (estado.transiciones.get('_') != null) {
                for (Estado s : estado.transiciones.get('_')) {
                    transicion_epsilon(s);
                }
            }
        }
    }
    /*
    //MINIMIZACIÓN
    private String nombre;
    private int numestados;
    private int estadoInicial;
    private TreeSet<String> alfabetox;
    private TreeSet<Integer> estadoFinal;
    private TreeSet<Integer>[][] tabtrans;

    public AutomataM minimizar(AutomataM automata) {

        nombre = automata.getNombre();
        numestados = automata.getnumEstados();
        alfabetox = automata.getAlfabeto();
        estadoInicial = automata.getEstadoInicial();
        estadoFinal = automata.getestadoFinal();
        tabtrans = automata.getTablaTransiciones();

        while (!verificarMinimo()) {
            minimizar();
        }

        JOptionPane.showMessageDialog(null, "Es minimo");

        return new AutomataM(numestados, alfabetox, estadoInicial, estadoFinal, tabtrans);

    }
    
    private TreeSet<Integer> obtenerTransicion(int q0, String e)
	{
		Vector<String> a = new Vector<String>();
		a.addAll(alfabeto);
                //System.out.println(tabtrans[q0][a.indexOf(e)]);
		return tabtrans[q0][a.indexOf(e)];
	}

    private boolean verificarMinimo() {
        boolean f = true;

        int[][] estados = new int[numestados][numestados];
        TreeSet<Integer> r;
        TreeSet<Integer> t;
        int y;
        int x;
        int tamanio = 0;
        for (int cont = 1; cont < numestados; cont++) {
            for (int cont2 = 0; cont2 < cont; cont2++) {
                if ((estadoFinal.contains(cont) && !estadoFinal.contains(cont2)) || (estadoFinal.contains(cont2) && !estadoFinal.contains(cont))) {
                    estados[cont][cont2] = 1;
                }
                tamanio = 0;
                for (String s : alfabeto) {
                    r = obtenerTransicion(cont, s);
                    t = obtenerTransicion(cont2, s);
                    if (r.size() > 0 && t.size() > 0) {
                        tamanio++;
                        y = r.first().intValue();
                        x = t.first().intValue();

                        if (x < y) {
                            if (estados[y][x] == 1) {
                                estados[cont][cont2] = 1;
                            }
                        } else {
                            if (estados[x][y] == 1) {
                                estados[cont][cont2] = 1;
                            }
                        }
                        if (y != x) {
                            estados[cont][cont2] = 1;
                        }
                    }
                }
                if (tamanio != alfabeto.size()) {
                    estados[cont][cont2] = 1;
                }
            }
        }

        for (int cont = 1; cont < numestados; cont++) {
            for (int cont2 = 0; cont2 < cont; cont2++) {
                if (estados[cont][cont2] == 0) {
                    f = false;
                }
            }
        }

        return f;

    }

    private void minimizar() {

        int[][] estados = new int[numestados][numestados];
        TreeSet<Integer> r;
        TreeSet<Integer> t;
        int x;
        int y;
        int tamanio = 0;
        for (int cont = 1; cont < numestados; cont++) {
            for (int cont2 = 0; cont2 < cont; cont2++) {
                if ((estadoFinal.contains(cont) && !estadoFinal.contains(cont2)) || (estadoFinal.contains(cont2) && !estadoFinal.contains(cont))) {
                    estados[cont][cont2] = 1;
                }
                tamanio = 0;
                for (String s : alfabeto) {
                    r = obtenerTransicion(cont, s);
                    t = obtenerTransicion(cont2, s);
                    if (r.size() > 0 && t.size() > 0) {
                        tamanio++;
                        x = r.first().intValue();
                        y = t.first().intValue();

                        if (y < x) {
                            if (estados[x][y] == 1) {
                                estados[cont][cont2] = 1;
                            }
                        } else {
                            if (estados[y][x] == 1) {
                                estados[cont][cont2] = 1;
                            }
                        }
                        if (x != y) {
                            estados[cont][cont2] = 1;
                        }
                    }
                }
                if (tamanio != alfabeto.size()) {
                    estados[cont][cont2] = 1;
                }
            }
        }
        Vector<TreeSet> vector = new Vector<TreeSet>();
        TreeSet<Integer> ts;
        boolean f;

        for (int cont = 1; cont < numestados; cont++) {
            for (int cont2 = 0; cont2 < cont; cont2++) {
                if (estados[cont][cont2] == 0) {
                    ts = new TreeSet<Integer>();
                    f = true;

                    ts.add(cont);
                    ts.add(cont2);

                    for (TreeSet<Integer> tsmod : vector) {
                        if (tsmod.contains(cont) || tsmod.contains(cont)) {
                            tsmod.addAll(ts);
                            f = false;
                        }
                    }
                    if (f) {
                        vector.add(ts);
                    }
                }
            }
        }

        f = true;

        for (int cont = 0; cont < numestados; cont++) {
            f = true;
            for (TreeSet<Integer> tsmod : vector) {
                if (tsmod.contains(cont)) {
                    f = false;
                }
            }
            if (f) {
                ts = new TreeSet<Integer>();
                ts.add(cont);
                vector.add(ts);
            }
        }

        TreeSet<Integer>[][] tablaTemp = new TreeSet[vector.size()][alfabeto.size()];

        TreeSet<Integer> tran;
        int t0;
        TreeSet<Integer> t1;
        for (String s : alfabeto) {
            for (TreeSet<Integer> tsi : vector) {
                tran = new TreeSet<Integer>();
                for (Integer i : tsi) {
                    tran.addAll(obtenerTransicion(i, s));
                }

                t0 = vector.indexOf(tsi);
                t1 = new TreeSet<Integer>();
                for (TreeSet<Integer> tsi2 : vector) {
                    if (tran.size() > 0 && tsi2.containsAll(tran)) {
                        t1.add(vector.indexOf(tsi2));
                    }
                }

                Vector<String> a = new Vector<String>();
                a.addAll(alfabeto);
                tablaTemp[t0][a.indexOf(s)] = t1;

            }
        }

        TreeSet<Integer> finales = new TreeSet<Integer>();
        int q00 = estadoInicial;

        for (TreeSet<Integer> i : vector) {
            if (i.contains(estadoInicial)) {
                q00 = vector.indexOf(i);
            }

            for (Integer ii : estadoFinal) {
                if (i.contains(ii)) {
                    finales.add(vector.indexOf(i));
                }
            }
        }

        estadoInicial = q00;
        numestados = vector.size();
        estadoFinal = finales;
        tabtrans = tablaTemp;

        System.out.println();

    }
     */
}
