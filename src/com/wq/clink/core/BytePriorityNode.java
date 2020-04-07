package com.wq.clink.core;

/**
 * Author: weiqiang
 * Time: 2020/4/7 上午10:39
 */
public class BytePriorityNode<Item> {
    public byte prioity;
    public Item item;
    public BytePriorityNode<Item> next;

    public BytePriorityNode(Item item) {
        this.item = item;
    }
    public void appendWithPriority(BytePriorityNode<Item> node){
        if (next==null){
            next =node;
        }else {
            BytePriorityNode<Item> after = this.next;
            if (after.prioity < node.prioity){
                this.next = node;
                node.next = after;
            }else {
                after.appendWithPriority(node);
            }
        }
    }
}
