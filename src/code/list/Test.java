package code.list;

public class Test {

    public static void main(String[] args){
        int[] arr = new int[]{1, 2, 3, 4, 5};
        ListNode listNode = createListNode(arr);
        System.out.println("原始链表："+lookListNode(listNode));

        //反转链表
        ListNode reverseListNode = reverseListNode(createListNode(arr));
        System.out.println("反转链表："+lookListNode(reverseListNode));

        //K 个一组翻转链表
        ListNode reverseKGroup2 = reverseKGroup(createListNode(arr), 2);
        ListNode reverseKGroup3 = reverseKGroup(createListNode(arr), 3);
        System.out.println("2个一组反转链表："+lookListNode(reverseKGroup2));
        System.out.println("3个一组反转链表："+lookListNode(reverseKGroup3));

        //两数相加 342 + 465 = 807
        ListNode node1 = createListNode(new int[]{2,4,3});
        ListNode node2 = createListNode(new int[]{5,6,4});
        ListNode addTwoNumbers = addTwoNumbers(node1, node2);
        System.out.println("两数相加后链表："+lookListNode(addTwoNumbers));


    }

    /**
     * 两数相加
     * @param node1
     * @param node2
     * @return
     */
    public static ListNode addTwoNumbers(ListNode node1, ListNode node2){
        ListNode pre = new ListNode(0),cur = pre;
        int carry = 0;
        while (node1 != null || node2 != null || carry != 0){
            int x = node1 == null ? 0 : node1.val;
            int y = node2 == null ? 0 : node2.val;
            int sum = x + y + carry;
            carry = sum / 10;
            sum = sum % 10;
            cur.next = new ListNode(sum);
            cur = cur.next;
            if(node1!=null){
                node1 = node1.next;
            }
            if(node2!=null){
                node2 = node2.next;
            }
        }
        return pre.next;
    }


    /**
     * K 个一组翻转链表
     * @param head
     * @param k
     * @return
     */
    public static ListNode reverseKGroup(ListNode head, int k) {
        if(head == null || head.next == null){
            return head;
        }
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode pre = dummy, end = dummy;
        while (end.next != null){
            for(int i=0; i<k && end!=null; i++){
                end = end.next;
            }
            if(end == null){
                break;
            }
            ListNode next = end.next;
            end.next = null;
            ListNode start = pre.next;
            pre.next = reverseListNode(start);
            start.next = next;
            pre = start;
            end = start;
        }
        return dummy.next;
    }


    /**
     * 反转链表
     * @param head
     * @return
     */
    public static ListNode reverseListNode(ListNode head){
        ListNode pre = null, cur = head;
        while (cur != null){
            ListNode next = cur.next;
            cur.next = pre;
            pre = cur;
            cur = next;
        }
        return pre;
    }


    /**
     * 创建链表
     * @param arr
     * @return
     */
    public static ListNode createListNode(int[] arr){
        ListNode head = new ListNode(arr[0]);
        ListNode cur = head;
        for(int i = 1; i < arr.length; i ++){
            cur.next = new ListNode(arr[i]);
            cur = cur.next;
        }
        return head;
    }

    /**
     * 查看ListNode的顺序结构
     * @param head
     * @return
     */
    public static String lookListNode(ListNode head){
        StringBuilder str = new StringBuilder();
        ListNode temp = head;
        while (temp != null){
            str.append(String.valueOf(temp.val));
            temp = temp.next;
        }
        return str.toString();
    }

}
