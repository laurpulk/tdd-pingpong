package pingis.utils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.comments.LineComment;

public class CodeStubBuilder {
  final CompilationUnit compilationUnit;
  final ClassOrInterfaceDeclaration clazz;
  String className;
  String filename;

  public CodeStubBuilder(String className) {
    compilationUnit = new CompilationUnit();
    this.className = className;
    filename = String.format("src/%s.java", className);

    clazz = compilationUnit.addClass(className);
  }

  public CodeStubBuilder withImport(String name) {
    compilationUnit.addImport(name);

    return this;
  }

  public CodeStubBuilder withBodyComment(String comment) {
    // Hack to add some nice whitespace between // and the comment text
    if (!comment.startsWith(" ")) {
      comment = " " + comment;
    }

    LineComment commentNode = new LineComment(comment);
    clazz.addOrphanComment(commentNode);

    return this;
  }

  public CodeStub build() {
    return new CodeStub(className, filename, compilationUnit.toString());
  }
}
