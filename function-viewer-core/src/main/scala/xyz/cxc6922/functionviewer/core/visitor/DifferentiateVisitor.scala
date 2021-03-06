package xyz.cxc6922.functionviewer.core.visitor

import xyz.cxc6922.functionviewer.core.ast._

import scala.beans.BeanProperty

class DifferentiateVisitor(@BeanProperty var toDifferentiate: Set[String] = Set())
  extends Visitor[Node] {

  private val cloneVisitor = new CloneVisitor

  private def cloneNode(node: Node): Node = {
    node.accept(cloneVisitor)
  }

  override def visit(node: Node): Node = {
    // alert error
    node
  }

  override def visit(node: ConstantNode): ConstantNode = {
    new ConstantNode(0, ConstantNode.Type.Zero)
  }

  override def visit(node: VariableNode): Node = {
    if (toDifferentiate.contains(node.name)) {
      new ConstantNode(1, ConstantNode.Type.One)
    } else {
      new ConstantNode(ConstantNode.Type.Zero)
    }
  }

  override def visit(node: NegativeNode): Node = {
    new NegativeNode(differentiateNode(node.child))
  }

  override def visit(node: PlusNode): PlusNode = {
    new PlusNode(
      differentiateNode(node.left),
      differentiateNode(node.right)
    )
  }

  override def visit(node: MinusNode): MinusNode = {
    new MinusNode(
      differentiateNode(node.left),
      differentiateNode(node.right)
    )
  }

  override def visit(node: MultiplyNode): PlusNode = {
    new PlusNode(
      new MultiplyNode(
        differentiateNode(node.left),
        cloneNode(node.right)
      ),
      new MultiplyNode(
        cloneNode(node.left),
        differentiateNode(node.right)
      )
    )
  }

  override def visit(node: DivideNode): DivideNode = {
    new DivideNode(
      new MinusNode(
        new MultiplyNode(
          differentiateNode(node.left),
          cloneNode(node.right)
        ),
        new MultiplyNode(
          cloneNode(node.left),
          differentiateNode(node.right)
        )
      ),
      new MultiplyNode(
        cloneNode(node.right),
        cloneNode(node.right)
      )
    )
  }

  def differentiateNode(node: Node): Node = {
    node.accept(this)
  }
}
