/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo.Automata;

import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author matiassebastianparra
 */
public class AFND {

    public Automata automata;
    Stack<Automata> pila = new Stack();
    String expresion;
    ArrayList<String> alfabeto;
    ArrayList<Integer> listaEstados;
    Entrada entrada;
    ArrayList<String> evalua;

    public AFND() {

    }

    public AFND(String expresion) {
        this.alfabeto = new ArrayList();
        this.listaEstados = new ArrayList();
        this.expresion = expresion;
        this.entrada = new Entrada();
        this.evalua = new ArrayList();
        setAlfabeto();
        this.generar_automata();
        setListaEstados();
        System.out.println(this.evalua);
    }

    public ArrayList significado(String expresion) {
        String tmp = "";
        for (int i = 0; i < this.expresion.length(); i++) {
            /* if (this.expresion.charAt(i) != '(' | this.expresion.charAt(i) != ')' | this.expresion.charAt(i) != '[' | this.expresion.charAt(i) != ']'
                    | this.expresion.charAt(i) != '{' | this.expresion.charAt(i) != '}' | this.expresion.charAt(i) != '+' | this.expresion.charAt(i) != '-'
                    | this.expresion.charAt(i) != '*' | this.expresion.charAt(i) != '|' | this.expresion.charAt(i) != '.') {
                tmp += this.expresion.charAt(i);
            }*/
            if (this.expresion.charAt(i) == '(' | this.expresion.charAt(i) == ')' | this.expresion.charAt(i) == '[' | this.expresion.charAt(i) == ']'
                    | this.expresion.charAt(i) == '{' | this.expresion.charAt(i) == '}' | this.expresion.charAt(i) == '+' | this.expresion.charAt(i) == '-'
                    | this.expresion.charAt(i) == '*' | this.expresion.charAt(i) == '|' | this.expresion.charAt(i) == '.') {
                this.evalua.add(tmp);
                this.evalua.add(Character.toString(this.expresion.charAt(i)));
                tmp = "";
            } else {
                tmp += this.expresion.charAt(i);
            }
        }
        this.evalua.add(tmp);
        return evalua;
    }

    public void generar_automata() {
        significado(expresion);
        for (int i = 0; i < this.evalua.size(); i++) {
            String c = this.evalua.get(i);
            switch (c) {
                case ".":
                    this.generar_automata_concatenacion();
                    break;
                case "|":
                    this.generar_automata_union();
                    break;
                case "*":
                    this.generar_automata_kleene();
                    break;
                default:
                    generar_automata_basico(c);
                    break;
            }
        }

        this.automata = this.pila.pop();

        //Se reinician los valores boolean que indican si el estado es de inicio 
        // o de aceptacion que tiene cada estado para luego asignarlos
        //de manera correcta
        for (Estado s : this.automata.estados) {
            s.inicio = false;
            s.fin = false;
        }

        //se indica mediante un boolean el estado de inicio del automata generado
        this.automata.inicio.setInicio(true);

        //se indican los estados finales correspondientes del automata mediante 
        //booleans
        for (Estado s : this.automata.finales) {
            s.setFin(true);
        }
    }

    public String imprimir_automata() {
        String respuesta = "";
        respuesta += "" + "\n";
        respuesta += "AFND" + "\n";
        respuesta += "K = { ";
        for (int i = 0; i < this.listaEstados.size(); i++) {
            if (this.listaEstados.size() - 1 == i) {
                respuesta += "q" + listaEstados.get(i);
            } else {
                respuesta += "q" + listaEstados.get(i) + ",";
            }
        }
        respuesta += " }" + "\n";
        respuesta += "Sigma = ";
        respuesta += getAlfabeto() + "\n";
        respuesta += "Delta :" + "\n";
        for (Estado estado : this.automata.estados) {
            respuesta += estado.imprimir_transiciones();
        }
        respuesta += ("s = { q" + this.automata.inicio.id + " }" + "\n");
        respuesta += ("F = { ");
        for (int i = 0; i < this.automata.finales.size(); i++) {
            if (this.automata.finales.size() - 1 == i) {
                respuesta += ("q" + this.automata.finales.get(i).id);
            } else {
                respuesta += ("q" + this.automata.finales.get(i).id + ",");
            }
        }
        respuesta += " }";
        return respuesta;
    }

    public void generar_automata_basico(String c) {
        Automata basico = new Automata();
        Estado estado1 = new Estado(0, true, false);
        Estado estado2 = new Estado(1, false, true);

        estado1.agregarTransicion(c, estado2);

        basico.agregarEstado(estado1);
        basico.agregarEstado(estado2);

        basico.setEstadoInicio(estado1);
        basico.agregarEstadoFinal(estado2);

        this.pila.push(basico);
    }

    public void generar_automata_union() {

        Automata automata2 = this.pila.pop();
        Automata automata1 = this.pila.pop();
        Automata union = new Automata();

        Estado inicio = new Estado(0, true, false);
        Estado fin = new Estado(0, false, true);

        inicio.agregarTransicion("_", automata1.inicio);
        inicio.agregarTransicion("_", automata2.inicio);

        automata1.finales.get(0).agregarTransicion("_", fin);
        automata2.finales.get(0).agregarTransicion("_", fin);

        //se agregan los estados que conformaran el nuevo automata 
        //para luego modificar sus id
        union.agregarEstado(inicio);

        for (Estado estado : automata1.estados) {
            union.agregarEstado(estado);
        }

        for (Estado estado : automata2.estados) {
            union.agregarEstado(estado);
        }

        union.agregarEstado(fin);

        //se actualizan los id
        for (int i = 0; i < union.estados.size(); i++) {
            union.estados.get(i).id = i;
        }

        union.inicio = inicio;
        union.agregarEstadoFinal(fin);

        this.pila.push(union);
    }

    public void generar_automata_concatenacion() {
        Automata automata1;
        Automata automata2;
        Automata concatenacion = new Automata();

        automata2 = this.pila.pop();
        automata1 = this.pila.pop();

        //se crea la transicion entre los dos automatas.
        automata1.finales.get(0).agregarTransicion("_", automata2.inicio);

        //se agregan todos los estados al automata de concatenacion.
        for (Estado estado : automata1.estados) {
            concatenacion.agregarEstado(estado);
        }

        for (Estado estado : automata2.estados) {
            concatenacion.agregarEstado(estado);
        }

        //se definen los estados de inicio y fin del nuevo automata
        concatenacion.setEstadoInicio(automata1.inicio);
        concatenacion.finales.addAll(automata2.finales);

        //se actualizan los id de los estados para el nuevo automata generado
        for (int i = 0; i < concatenacion.estados.size(); i++) {
            concatenacion.estados.get(i).id = i;
        }

        this.pila.push(concatenacion);
    }

    public void generar_automata_kleene() {
        Automata automata = this.pila.pop();
        Automata kleene = new Automata();

        Estado inicial = new Estado(0, true, false);
        Estado fin = new Estado(0, false, true);

        inicial.agregarTransicion("_", automata.inicio);
        inicial.agregarTransicion("_", fin);

        automata.finales.get(0).agregarTransicion("_", fin);
        automata.finales.get(0).agregarTransicion("_", automata.inicio);

        kleene.setEstadoInicio(inicial);
        kleene.agregarEstadoFinal(fin);

        kleene.agregarEstado(inicial);

        for (Estado estado : automata.estados) {
            kleene.agregarEstado(estado);
        }

        kleene.agregarEstado(fin);

        for (int i = 0; i < kleene.estados.size(); i++) {
            kleene.estados.get(i).id = i;
        }

        this.pila.push(kleene);
    }

    public Automata getAutomata() {
        return this.automata;
    }

    public String getExpresion() {
        return expresion;
    }

    public ArrayList<String> getAlfabeto() {
        return alfabeto;
    }

    public void setAlfabeto() {
        this.significado(this.expresion);
        for (int i = 0; i < this.evalua.size(); i++) {
            String c = this.evalua.get(i);
            if (c != "|" && c != "+" && c != "*" && c != "." && c != "_" && c != "0") {
                if (!this.alfabeto.contains(c)) {
                    this.alfabeto.add(c);
                }
            }
        }
    }

    public ArrayList<Integer> getListaEstados() {
        return this.listaEstados;
    }

    private void setListaEstados() {
        for (int i = 0; i < this.automata.estados.size(); i++) {
            Estado estado = this.automata.estados.get(i);
            this.listaEstados.add(estado.id);
        }
    }
}
