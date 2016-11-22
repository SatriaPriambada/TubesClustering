/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testtubesclassifier;

/**
 *
 * @author programtak from http://programtalk.com/java/java-tree-implementation/
 */
import java.util.ArrayList;
import java.util.List;
 
public class Node {
 private String id;
 private final List<Node> children = new ArrayList<>();
 private final Node parent;
 
 public Node(Node parent) {
  this.parent = parent;
 }
 
 public String getId() {
  return id;
 }
 
 public void setId(String id) {
  this.id = id;
 }
 
 public List<Node> getChildren() {
  return children;
 }
 
 public Node getParent() {
  return parent;
 }
 
}
