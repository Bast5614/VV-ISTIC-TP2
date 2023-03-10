package fr.istic.vv;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;


// This class visits a compilation unit and
// prints all public enum, classes or interfaces along with their public methods
public class PublicElementsPrinter extends VoidVisitorWithDefaults<Void> {

    @Override
    public void visit(CompilationUnit unit, Void arg) {
        for(TypeDeclaration<?> type : unit.getTypes()) {
            type.accept(this, null);
        }
    }

    public void visitTypeDeclaration(TypeDeclaration<?> declaration, Void arg) {
        if(!declaration.isPublic()) return;
        System.out.println(declaration.getFullyQualifiedName().orElse("[Anonymous]"));
        for(MethodDeclaration method : declaration.getMethods()) {
            method.accept(this, arg);
        }
        // Printing nested types in the top level
        for(BodyDeclaration<?> member : declaration.getMembers()) {
            if (member instanceof TypeDeclaration)
                member.accept(this, arg);
        }
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
        visitTypeDeclaration(declaration, arg);
    }

    @Override
    public void visit(EnumDeclaration declaration, Void arg) {
        visitTypeDeclaration(declaration, arg);
    }

    @Override
    public void visit(MethodDeclaration declaration, Void arg) {
        if(!declaration.isPublic()) return;
        System.out.println("  " + declaration.getDeclarationAsString(true, true));
    }

    public class PrivateFieldReport {

        public static void main(String[] args) throws FileNotFoundException {
            if (args.length < 1) {
                System.out.println("Usage: java PrivateFieldReport <path-to-source>");
                return;
            }

            String path = args[0];
            File file = new File(path);
            if (!file.exists()) {
                System.out.println("Error: File not found: " + path);
                return;
            }

            List<File> javaFiles = new ArrayList<>();
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    if (f.isFile() && f.getName().endsWith(".java")) {
                        javaFiles.add(f);
                    }
                }
            } else if (file.getName().endsWith(".java")) {
                javaFiles.add(file);
            } else {
                System.out.println("Error: Invalid file type: " + path);
                return;
            }

            List<String[]> report = new ArrayList<>();
            for (File javaFile : javaFiles) {
                try {
                    CompilationUnit cu = StaticJavaParser.parse(javaFile);
                    List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);
                    for (ClassOrInterfaceDeclaration classDecl : classes) {
                        if (classDecl.isPublic()) {
                            List<FieldDeclaration> fields = classDecl.getFields();
                            for (FieldDeclaration field : fields) {
                                if (field.isPrivate() && !hasPublicGetter(field, classDecl)) {
                                    String[] row = new String[3];
                                    row[0] = field.getVariables().get(0).getNameAsString();
                                    row[1] = classDecl.getNameAsString();
                                    row[2] = cu.getPackageDeclaration().map(pd -> pd.getNameAsString()).orElse("");
                                    report.add(row);
                                }
                            }
                        }
                    }
                } catch (ParseException e) {
                    System.out.println("Error parsing file: " + javaFile.getPath());
                }
            }

            // Output report
            System.out.println("Private Fields Report\n");
            System.out.println("Field Name, Class Name, Package");
            for (String[] row : report) {
                System.out.println(String.join(", ", row));
            }
        }

        private static boolean hasPublicGetter(FieldDeclaration field, ClassOrInterfaceDeclaration classDecl) {
            String fieldName = field.getVariables().get(0).getNameAsString();
            String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            List<MethodDeclaration> methods = classDecl.getMethodsByName(getterName);
            if (methods.size() == 1) {
                MethodDeclaration method = methods.get(0);
                if (method.isPublic() && method.getParameters().isEmpty()) {
                    Type returnType = method.getType();
                    if (returnType instanceof PrimitiveType && ((PrimitiveType) returnType).getType() == PrimitiveType.Primitive.BOOLEAN) {
                        // Boolean getter
                        BlockStmt body = method.getBody().orElse(null);
                        return body != null && body.getStatements().size() == 1 && body.getStatement(0) instanceof ReturnStmt;
                    } else {
                        // Non-boolean getter
                        BlockStmt body = method.getBody().orElse(null);
                        return
}
