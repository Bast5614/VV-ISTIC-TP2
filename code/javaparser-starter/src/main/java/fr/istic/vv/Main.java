package fr.istic.vv;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Main {

    public static void main(String[] args) throws Exception {
        if(args.length == 0) {
            System.err.println("Should provide the path to the source code");
            System.exit(1);
        }

        File file = new File(args[0]);
        PrintWriter resultFile = new PrintWriter("src/main/java/fr/istic/vv/result.txt");

        CompilationUnit cu = StaticJavaParser.parse(file);
        for(ClassOrInterfaceDeclaration classe : cu.findAll(ClassOrInterfaceDeclaration.class)){
            for(VariableDeclarator variable : cu.findAll(VariableDeclarator.class)){
                String fieldName = String.valueOf(variable.getName());
                String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                if (!hasGetter(classe, getterName)) {
                    resultFile.println("La variable " + fieldName + " de la class " + classe.getNameAsString() + " n'a pas de getter public");
                }
            }
        }
        resultFile.close();
    }
    private static boolean hasGetter(ClassOrInterfaceDeclaration classe, String methodName) {
        for (MethodDeclaration method : classe.getMethods()) {
            if (method.getNameAsString().equals(methodName)) {
                return true;
            }
        }
        return false;
    }

}
