package fabric8;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.api.model.extensions.DeploymentBuilder;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.kubernetes.client.dsl.Resource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * 作者：sunna
 * 时间: 2018/5/23 11:20
 */
public class TestFabric8ForK8s
{
    private static KubernetesClient client;
    private  static String namespace = "default";

    @BeforeClass
    public static void setUp()
    {
        String master = "http://192.168.56.101:8001/";  // API Server地址

        //配置类
        Config config = new ConfigBuilder()
                .withMasterUrl(master)
                .withNamespace(namespace)
                .build();

        //初始化Client
        client = new DefaultKubernetesClient(config);
    }

    @AfterClass
    public static void tearDown()
    {
        client.close();
    }


    /**
     * 获取资源列表
     */
    @Test
    public void testGetListResources()
    {
        List<Pod> list = ((DefaultKubernetesClient) client).inAnyNamespace().pods().list().getItems();
        for (Pod item : list)
        {
            System.out.println(item.getMetadata().getName()); //getMetadata获取元数据
        }

        //所有命名空间
        NamespaceList myNs = client.namespaces().list();
        for (Namespace item : myNs.getItems())
        {
            System.out.println(item.getMetadata().getName());
        }

        //所有Service
        ServiceList myServices = client.services().list();
        for (Service item : myServices.getItems())
        {
            System.out.println(item.getMetadata().getName());
        }

        //default命名空间下的Service
        ServiceList myNsServices = client.services().inNamespace(namespace).list();
        for (Service item : myNsServices.getItems())
        {
            System.out.println(item.getMetadata().getName());
        }
    }

    /**
     * 删除资源
     */
    @Test
    public void testDelete()
    {
        Boolean ret = client.namespaces().withName("myns").delete();
        System.out.println(ret);

        ret = client.services().inNamespace(namespace).withName("myservice").delete();
        System.out.println(ret);
    }


    /**
     * 创建资源
     */
    @Test
    public void createResources()
    {
        //使用createNew方法
        //在editMetadata与endMetadata之间设置
        //done结束
        Namespace myns = client.namespaces().createNew()
                .withNewMetadata()
                .withName("namespace-test")
                .addToLabels("a", "label")
                .endMetadata()
                .done();

        Service myservice = client.services().inNamespace(namespace).createNew()
                .withNewMetadata()
                .withName("myservice")
                .addToLabels("another", "label")
                .endMetadata()
                .done();
    }

    /**
     * 修改资源
     */
    @Test
    public void editResources()
    {
        //使用edit方法
        //在editMetadata与endMetadata之间设置
        //done结束
        Namespace myns = client.namespaces().withName("myns").edit()
                .editMetadata()
                .addToLabels("a", "label")
                .endMetadata()
                .done();

        Service myservice = client.services().inNamespace(namespace).withName("myservice").edit()
                .editMetadata()
                .addToLabels("another", "label")
                .endMetadata()
                .done();
    }



    /**
     * 根据条件查询namespace
     */
    @Test
    public void testGetNameSpaceByCondition()
    {
        Namespace namespace = client.namespaces().withName("default").get();
        System.out.println(namespace.getMetadata().getName());
    }

    /**
     * 使用yaml资源文件创建Pod
     */
    @Test
    public void testCreateNamespace() throws FileNotFoundException
    {
        //使用load()加载资源文件，资源类型为HasMetadata
        File file = new File("D:\\MyCode\\Self\\idea\\studyproject\\src\\test\\resources\\test_create_pod.yaml");
        InputStream input = new FileInputStream(file);
        List<HasMetadata> resources  = client.load(input).get();

        //只有一个资源文件，
        HasMetadata resource = resources.get(0);

        //是否为Pod资源
        if (resource instanceof Pod){
            Pod pod = (Pod) resource;

            //资源操作句柄
            NonNamespaceOperation<Pod, PodList, DoneablePod, PodResource<Pod, DoneablePod>> pods = client.pods().inNamespace(namespace);

            //创建
            Pod result = pods.create(pod);
            System.out.println("Created pod " + result.getMetadata().getName());

            //删除Pod
            client.pods().inNamespace(namespace).withName("testpod").delete();
            //client.pods().withLabel("this", "works").delete();
        }

    }

    /**
     * 测试Deployment
     *
     * Deployment属于extensions资源，除此之外还有daemonSets, jobs, ingresses等
     * extensions资源的CRUD和Namespace、Service不同
     */
    @Test
    public void testDeployment()
    {
        Deployment deployment = new DeploymentBuilder()
                .withNewMetadata()
                .withName("nginx")
                .endMetadata()
                .withNewSpec()
                .withReplicas(1)
                .withNewTemplate()
                .withNewMetadata()
                .addToLabels("app", "nginx")
                .endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withName("nginx")
                .withImage("nginx")
                .addNewPort()
                .withContainerPort(80)
                .endPort()
                .endContainer()
                .endSpec()
                .endTemplate()
                .endSpec()
                .build();


        deployment = client.extensions().deployments().inNamespace(namespace).create(deployment);
        System.err.println("Scaling up:" + deployment.getMetadata().getName());

        //扩展
        client.extensions().deployments().inNamespace(namespace).withName("nginx").scale(2, true);

        //删除
        client.resource(deployment).delete();
    }


    /**
     * ConfigMap与Service、NameSpace是一类
     */
    @Test
    public void testConfigMap()
    {
        //这里的withName有什么作用？
        Resource<ConfigMap, DoneableConfigMap> configMapResource = client.configMaps().inNamespace(namespace).withName("test-configmap");

        //创建或更新
        ConfigMap configMap = configMapResource.createOrReplace(new ConfigMapBuilder()
                .withNewMetadata()
                .withName("test")
                .endMetadata()
                .addToData("foo", "" + new Date())
                .addToData("bar", "beer")
                .build());

        System.out.println("Upserted ConfigMap at " + configMap.getMetadata().getSelfLink() + " data " + configMap.getData());
    }
}
