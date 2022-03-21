package com.lucky.design;

import java.util.List;

/**
 * @author: Loki
 * @data: 2021-11-24 17:24
 **/
public class Node {
    public Integer val ;
    public Node next;
    public Node left;
    public Node right;
    public List<Node> children;
    public Node parent;
    public Node(Integer val) {
        this.val = val;
    }
    public Node(Integer val,List<Node> children){
        this.val = val;
        this.children = children;
    }
}
