package consistenthashing;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

public class HashRingTest {
        @Test
        void testAddNodes() {
                // test nodes not added to empty hashring
                Node node = new Node("node1");
                HashRing ring = new HashRing(0);
                ring.addNode(node);
                assertEquals("{}", ring.toString());

                // test node added to hashring
                ring = new HashRing(1);
                ring.addNode(node);
                String temp = "{2114402076=VirtualNode{id='node1#0', "
                                + "node=node1, hashPosition=2114402076}}";
                assertEquals(temp, ring.toString());

                // test multiple nodes added to hashring
                Node node2 = new Node("node2");
                Node node3 = new Node("node3");
                ring = new HashRing(2);
                ring.addNode(node2);
                ring.addNode(node3);
                temp = "{2114403037=VirtualNode{id='node2#0', node=node2, hashPosition=2114403037}, "
                                + "2114403038=VirtualNode{id='node2#1', node=node2, hashPosition=2114403038}, "
                                + "2114403998=VirtualNode{id='node3#0', node=node3, hashPosition=2114403998}, "
                                + "2114403999=VirtualNode{id='node3#1', node=node3, hashPosition=2114403999}}";
                assertEquals(temp, ring.toString());
        }

        @Test
        void removeNodes() {
                // test one node removed
                Node node = new Node("node1");
                HashRing ring = new HashRing(1);
                ring.addNode(node);
                String temp = "{2114402076=VirtualNode{id='node1#0', "
                                + "node=node1, hashPosition=2114402076}}";
                assertEquals(temp, ring.toString());
                ring.removeNode(node);
                assertEquals("{}", ring.toString());

                // test multiple nodes removed
                Node node2 = new Node("node2");
                Node node3 = new Node("node3");
                ring = new HashRing(2);
                ring.addNode(node2);
                ring.addNode(node3);
                temp = "{2114403037=VirtualNode{id='node2#0', node=node2, hashPosition=2114403037}, "
                                + "2114403038=VirtualNode{id='node2#1', node=node2, hashPosition=2114403038}, "
                                + "2114403998=VirtualNode{id='node3#0', node=node3, hashPosition=2114403998}, "
                                + "2114403999=VirtualNode{id='node3#1', node=node3, hashPosition=2114403999}}";
                assertEquals(temp, ring.toString());
                ring.removeNode(node2);
                temp = "{2114403998=VirtualNode{id='node3#0', node=node3, hashPosition=2114403998}, "
                                + "2114403999=VirtualNode{id='node3#1', node=node3, hashPosition=2114403999}}";
                assertEquals(temp, ring.toString());
        }
}