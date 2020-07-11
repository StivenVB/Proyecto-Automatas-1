/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo.Automata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

/**
 *
 * @author Mn_go
 */
public class Parser {

    private Map<String, Integer> precedencia;
    public String aux;
    ArrayList<String> evalua;

    public Parser(String expresion) {
        Map<String, Integer> map = new HashMap<>();
        this.evalua = new ArrayList();
        map.put("(", 1);
        map.put("|", 2);
        map.put(".", 3);
        map.put("*", 4);
        precedencia = Collections.unmodifiableMap(map);

        setAux(convertir_expresion(expresion));
    }

    public ArrayList significado(String expresion) {
        String tmp = "";
        for (int i = 0; i < expresion.length(); i++) {
            /* if (this.expresion.charAt(i) != '(' | this.expresion.charAt(i) != ')' | this.expresion.charAt(i) != '[' | this.expresion.charAt(i) != ']'
                    | this.expresion.charAt(i) != '{' | this.expresion.charAt(i) != '}' | this.expresion.charAt(i) != '+' | this.expresion.charAt(i) != '-'
                    | this.expresion.charAt(i) != '*' | this.expresion.charAt(i) != '|' | this.expresion.charAt(i) != '.') {
                tmp += this.expresion.charAt(i);
            }*/
            if (expresion.charAt(i) == '(' | expresion.charAt(i) == ')' | expresion.charAt(i) == '[' | expresion.charAt(i) == ']'
                    | expresion.charAt(i) == '{' | expresion.charAt(i) == '}' | expresion.charAt(i) == '+' | expresion.charAt(i) == '-'
                    | expresion.charAt(i) == '*' | expresion.charAt(i) == '|' | expresion.charAt(i) == '.') {
                this.evalua.add(tmp);
                this.evalua.add(Character.toString(expresion.charAt(i)));
                tmp = "";
            } else {
                tmp += expresion.charAt(i);
            }
        }
        this.evalua.add(tmp);
        return evalua;
    }

    public String convertir_expresion(String expresion) {
        Stack<String> stack = new Stack<>();
        significado(expresion);
        String postfix = "";

        for (int i = 0; i < this.evalua.size(); i++) {
            String c = this.evalua.get(i);
            switch (c) {

                case "(":
                    stack.push(c);
                    break;

                case ")":
                    while (!stack.peek().equals("(")) {
                        postfix += stack.pop();
                    }
                    stack.pop();
                      break;
    /*                       case "[":
                    stack.push(c);
                    break;

                case "]":
                    while (!stack.peek().equals("[")) {
                        postfix += stack.pop();
                    }
                    stack.pop();
                    break;
 
                case "{":
                    stack.push(c);
                    break;

                case "}":
                    while (!stack.peek().equals("{")) {
                        postfix += stack.pop();
                    }
                    stack.pop();
                    break;
                 */
                default:
                    while (stack.size() > 0) {
                        String peekedChar = stack.peek();
                        Integer peekedCharPrecedence = obtenerPrecedencia(peekedChar);
                        Integer currentCharPrecedence = obtenerPrecedencia(c);
                        if (peekedCharPrecedence >= currentCharPrecedence) {
                            postfix += stack.pop();

                        } else {
                            break;
                        }
                    }
                    stack.push(c);
                    break;
            }
        }

        while (stack.size() > 0) {
            postfix += stack.pop();
        }

        return postfix;

    }

    public int obtenerPrecedencia(String c) {
        Integer precedencia_char = precedencia.get(c);

        if (precedencia_char == null) {
            precedencia_char = 6;
        }

        return precedencia_char;
    }

    public String getAux() {
        return aux;
    }

    public void setAux(String aux) {
        this.aux = aux;
    }

}
