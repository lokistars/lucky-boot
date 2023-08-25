package com.lucky.platform.rabbitMq;

import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author: Loki
 * @data: 2022-07-12 20:10
 */
public class HelloWorld {

    public HelloWorld() throws Exception {
    }
    final Connection connection = RabbitMqUtil.getConnection();
    /**
     * queue: 队列名
     * durable: 是否持久化,true 持久化
     * exclusive: 是否排它,true 排它是基于连接可见,该队列仅对首次声明它的连接可见,并在连接断开后自动删除
     * autoDelete: 是否自动删除,true 自动删除最后一个消息消费后队列是否被自动删除
     * arguments:  用来设置队列附加参数，如设置队列的有效期、队列的消息生命周期、消息的最大长度等；
     *  message-ttl:发布到队列的消息在被丢弃之前可以存活多长时间（毫秒）
     *  x-expires:队列在被自动删除之前可以未使用多长时间（毫秒）
     *  x-max-length:队列在开始从其头部删除之前可以包含多少（就绪）消息
     *  x-max-length-bytes:队列在开始从其头部掉落之前可以包含的就绪消息的总正文大小。
     *  x-overflow:设置队列溢出行为。这决定了当达到队列的最大长度时， reject-publish-dlx（拒绝发送消息到死信交换器）
     *  drop-head（删除queue头部的消息）,reject-publish（最近发来的消息将被丢弃）,
     *  x-dead-letter-exchange:消息被拒绝或过期时将重新发布到的交换的交换的可选名称。
     *  x-dead-letter-routing-key:当邮件是死信时使用的可选替换路由键。如果未设置，则将使用邮件的原始路由密钥
     *  x-single-active-consumer:如果设置，请确保一次只有一个使用者从队列中消耗，并在活动使用者被取消或死亡时故障转移到另一个注册使用者
     *  x-max-priority:队列要支持的最大优先级数;如果未设置，队列将不支持消息优先级。
     *  x-queue-mode:将队列设置为懒惰模式，在磁盘上保留尽可能多的消息以减少RAM使用量;如果未设置，队列将保留内存中的缓存，以尽可能快地传递消息。
     *  x-queue-master-locator:将队列设置为主位置模式，确定在节点群集上声明队列主服务器时所依据的规则。
     * hello world 使用的默认交换机,队列只能有一个消费者消费
     * @throws Exception
     */
    @Test
    public void helloPublish()throws Exception{
        // 构建Channel
        final Channel channel = connection.createChannel();
        // 构建队列,设置过期时间
        Map<String,Object> map = new HashMap<>(16);
        map.put("x-message-ttl",10000);
        map.put("x-max-length",3);
        channel.queueDeclare(RabbitMqUtil.HELLO_QUEUE,false,false,false,map);
        // 发布消息
        String message = "Hello World";
        AMQP.BasicProperties.Builder basic = new AMQP.BasicProperties().builder();
        basic.expiration("5000");
        basic.deliveryMode(1);  //设置消息持久化, 1:非持久化、2:持久化
        /**
         * exchange:要将消息发送到的Exchange(交换器)
         * routingKey:路由Key
         * mandatory:true 如果mandatory标记被设置
         * immediate: true 如果immediate标记被设置，注意：RabbitMQ服务端不支持此标记
         * props:其它的一些属性，如：{@link MessageProperties.PERSISTENT_TEXT_PLAIN}
         * body:消息内容
         **/
        channel.basicPublish("",RabbitMqUtil.HELLO_QUEUE,false,false,basic.build(),message.getBytes());
        System.out.println("消息发送成功");
        System.in.read();
    }

    @Test
    public void helloConsumer() throws Exception{
        consumer(RabbitMqUtil.HELLO_QUEUE);
        consumer(RabbitMqUtil.WORK_QUEUE);
        consumer(RabbitMqUtil.FANOUT_QUEUE);
        consumer(RabbitMqUtil.ROUTING_QUEUE);
        consumer(RabbitMqUtil.TOPIC_QUEUE);
        consumer(RabbitMqUtil.HEADERS_QUEUE);
        System.in.read();
    }
    /**
     * 消费者 一个队列可以有多个消费者
     * 公平分发, 在实际业务中，有些业务处理比较耗时，有些处理耗时不长
     * 设置消息的流控,费者处理完成之后主动上报 来达到公平分发
     * @throws Exception
     */
    public void consumer(String queue){
        CompletableFuture.runAsync(()->{
            try{
                // 1.获取连接、2.构建Channel
                final Channel channel = connection.createChannel();
                // 为这个通道申明一个队列，如果这个队列不存在，他将在服务器上创建，防止不存在时报错
                //channel.queueDeclare(queue,false,false,false,null);
                /**
                 * 设置消息的流控,一次获取多少条消息
                 * prefetchSize:服务器传送最大内容量（以八位字节计算），如果没有限制，则为0
                 * prefetchCount:服务器每次传递的最大消息数，如果没有限制，则为0；
                 * global:如果为true,则当前设置将会应用于整个Channel(频道)
                 **/
                channel.basicQos(1);
                final DefaultConsumer consumer = new DefaultConsumer(channel){
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        System.out.println("消费者一号获取消息："+queue +"："+ new String(body,"UTF-8"));
                        /**
                         * 手动ack,告诉rabbitmq已经消费了
                         * consumerTag:服务器端生成的消费者标识 服务器端向消费者推送消息，消息会携带一个deliveryTag参数，也可以成此参数为消息的唯一标识，是一个递增的正整数
                         * multiple：true表示确认所有消息，包括消息唯一标识小于等于deliveryTag的消息，false只确认deliveryTag指定的消息
                         */
                        channel.basicAck(envelope.getDeliveryTag(),false);
                        /**
                         * 要求代理重新发送未确认的消息,消息将会重新排队，并且可能会发送给其它的消费者
                         * requeue:如果为true,消息将会重新入队，可能会被发送给其它的消费者；如果为false,消息将会发送给* 相同的消费者
                         **/
                        //channel.basicRecover(true);
                        /**
                         * 拒绝接收到的一个或者多个消息
                         * deliveryTag：接收到消息的唯一标识
                         * multiple: true表示拒绝所有的消息，包括提供的deliveryTag；false表示仅拒绝提供的deliveryTag
                         * requeue：true 表示拒绝的消息应重新入队，而不是否丢弃
                         */
                        //channel.basicNack(envelope.getDeliveryTag(), false, true);
                        /**
                         * 拒绝接收到的一个或者多个消息
                         * deliveryTag：接收到消息的唯一标识
                         * requeue：true 表示拒绝的消息应重新入队，而不是否丢弃
                         */
                        //channel.basicReject(envelope.getDeliveryTag(), true);
                    }
                };
                /**
                 * false 关闭自动ack,默认情况下队列会以轮询的方式交给不同的消费者消费
                 *
                 * 启动一个消费者,并返回服务端生成的消费者标识
                 * true 接收到传递过来的消息后acknowledged（应答服务器），false 接收到消息后不应答服务器
                 * deliverCallback  当一个消息发送过来后的回调接口
                 * cancelCallback：当一个消费者取消订阅时的回调接口
                 */
                channel.basicConsume(queue,false,consumer);
                System.out.println("开始监听队列:"+queue);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    /**
     *  使用work queue
     * @throws Exception
     */
    @Test
    public void workPublish()throws Exception{
        // 构建Channel
        final Channel channel = connection.createChannel();
        // 构建队列
        channel.queueDeclare(RabbitMqUtil.WORK_QUEUE,false,false,false,null);
        // 发布消息
        for (int i = 0; i < 10; i++) {
            String message = "Hello World"+i;
            channel.basicPublish("",RabbitMqUtil.WORK_QUEUE,null,message.getBytes());
        }
        System.out.println("消息发送成功");
        //System.in.read();
    }

    /**
     * publish/subscribe 发布订阅模式 fanout 交换机无需设置routing key 交换机所绑定的队列都发送消息
     * durable：
     * autoDelete：
     * internal：
     * arguments：
     */
    @Test
    public void publish() throws Exception{
        // 2.构建Channel
        final Channel channel = connection.createChannel();
        // 3.构建交换机,交换机名称、交换机类型、交换机是否持久化
        channel.exchangeDeclare(RabbitMqUtil.FANOUT_NAME,BuiltinExchangeType.FANOUT,false,false,false,null);
        // 4.构建队列
        channel.queueDeclare(RabbitMqUtil.FANOUT_QUEUE,false,false,false,null);
        channel.queueDeclare(RabbitMqUtil.FANOUT_QUEUE1,false,false,false,null);
        // 5.绑定交换机
        channel.queueBind(RabbitMqUtil.FANOUT_QUEUE,RabbitMqUtil.FANOUT_NAME,"");
        channel.queueBind(RabbitMqUtil.FANOUT_QUEUE1,RabbitMqUtil.FANOUT_NAME,"");
        // 6.发送消息到交换机
        channel.basicPublish(RabbitMqUtil.FANOUT_NAME,"",null,"publish/subscribe".getBytes());
        System.out.println("消息成功发送！！");
    }

    /**
     * 在绑定Exchange和Queue时，需要指定好routingKey，同时在发送消息时，也指定routingKey，只有routingKey一致时，才会把指定的消息路由到指定的Queue
     */
    @Test
    public void routing() throws Exception{
        //2、创建channel
        Channel channel = connection.createChannel();

        //3、 构建交换机
        channel.exchangeDeclare(RabbitMqUtil.DIRECT_NAME,BuiltinExchangeType.DIRECT,false,false,false,null);
        //4. 构建队列
        channel.queueDeclare(RabbitMqUtil.ROUTING_QUEUE,false,false,false,null);
        //5. 绑定交换机和队列、队列名称、交换机名称 routingKey
        channel.queueBind(RabbitMqUtil.ROUTING_QUEUE,RabbitMqUtil.DIRECT_NAME,"ORANGE");
        channel.queueBind(RabbitMqUtil.ROUTING_QUEUE,RabbitMqUtil.DIRECT_NAME,"BLACK");

        // 6.发送消息到交换机、交换机名称、routingKey、设置参数、消息体
        channel.basicPublish(RabbitMqUtil.DIRECT_NAME,"ORANGE",null,"routingKey成功了".getBytes());
        channel.basicPublish(RabbitMqUtil.DIRECT_NAME,"BLACK",null,"routingKey成功了1".getBytes());
    }

    /**
     * TOPIC类型可以编写带有特殊意义的routingKey的绑定方式
     * @throws Exception
     */
    @Test
    public void topic() throws Exception{
        //2、创建channel
        Channel channel = connection.createChannel();

        //3、 构建交换机
        channel.exchangeDeclare(RabbitMqUtil.TOPIC_NAME,BuiltinExchangeType.TOPIC,false,false,false,null);
        //4. 构建队列
        channel.queueDeclare(RabbitMqUtil.TOPIC_QUEUE,false,false,false,null);
        //5. 绑定交换机和队列、队列名称、交换机名称 routingKey
        channel.queueBind(RabbitMqUtil.TOPIC_QUEUE,RabbitMqUtil.TOPIC_NAME,"*.orange.*");
        channel.queueBind(RabbitMqUtil.TOPIC_QUEUE,RabbitMqUtil.TOPIC_NAME,"lazy.#");

        // 6.发送消息到交换机、交换机名称、routingKey、设置参数、消息体
        channel.basicPublish(RabbitMqUtil.TOPIC_NAME,"big.orange.rabbit",null,"大橙子".getBytes());
        channel.basicPublish(RabbitMqUtil.TOPIC_NAME,"lazy.dog",null,"懒狗".getBytes());
    }

    @Test
    public void headers() throws Exception{
        //2、创建channel
        Channel channel = connection.createChannel();

        //3、 构建交换机
        channel.exchangeDeclare(RabbitMqUtil.HEADERS_NAME,BuiltinExchangeType.HEADERS,false,false,false,null);
        //4. 构建队列
        channel.queueDeclare(RabbitMqUtil.HEADERS_QUEUE,false,false,false,null);
        Map<String,Object>map = new HashMap<>(6);
        //any: 多个header的key-value只要可以匹配上一个就可以
        //all: 多个header的key-value要求全部匹配上！
        map.put("x-match","all");
        map.put("name","loki");
        //5. 绑定交换机和队列、队列名称、交换机名称 routingKey
        channel.queueBind(RabbitMqUtil.HEADERS_QUEUE,RabbitMqUtil.HEADERS_NAME,"",map);
        Map<String,Object> headers = new HashMap<>(6);
        headers.put("name","loki");
        AMQP.BasicProperties.Builder props = new AMQP.BasicProperties.Builder();
        props.headers(headers);
        // 6.发送消息到交换机、交换机名称、routingKey、设置参数、消息体
        channel.basicPublish(RabbitMqUtil.HEADERS_NAME,"",props.build(),"大橙子".getBytes());
    }
}
