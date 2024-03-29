package cn.bosenkeji.service.Impl;

import cn.bosenkeji.mapper.JobMapper;
import cn.bosenkeji.service.JobService;
import cn.bosenkeji.vo.Job;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.aliyuncs.schedulerx2.model.v20190430.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class JobServiceImpl implements JobService {

    private final Logger Log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private JobMapper jobMapper;

    @Value("${regionId}")
    private String regionId;

    @Value("${accessKeyId}")
    private  String accessKeyId;

    @Value("${accessKeySecret}")
    private  String accessKeySecret;

    @Value("${productName}")
    private  String productName;

    @Value("${domain}")
    private  String domain;

    @Value("${namespace}")
    private  String namespace;

    @Value("${groupId}")
    private  String groupId;

    @Value("${jobType}")
    private String jobType;

    @Value("${className}")
    private String className;
    //单机模式 广播模式 等等
    @Value("${executeMode}")
    private String executeMode;

    @Value("${timeType}")
    private int timeType;

    @Value("${sendChannel}")
    private String sendChannel;

    @Value("${channelUsername}")
    private String channelUsername;

    @Value("${channelUserPhone}")
    private String channelUserPhone;

    /*@Value("${timeExpression}")
    private String timeExpression;*/

    @Override
    public CreateJobResponse createJob(String jobName,String timeExpression,String parameters) throws ClientException {

        if(jobMapper.checkExistByJobName(jobName)>=1) {
            Log.info("任务"+jobName+"创建失败，因为任务调度"+jobName+"已存在！");
            CreateJobResponse createJobResponse = new CreateJobResponse();
            createJobResponse.setSuccess(false);
            createJobResponse.setMessage("任务"+jobName+"创建失败，因为任务调度"+jobName+"已存在！");
            return createJobResponse;
        }
        Log.info("进入创建调度任务");
        DefaultAcsClient defaultAcsClient = getDefaultAcsClient();
        CreateJobRequest request=new CreateJobRequest();
        request.setNamespace(namespace);
        request.setGroupId(groupId);
        request.setJobType(jobType);
        request.setClassName(className);
        //createJobRequest.setJarUrl(jarUrl);
        request.setName(jobName);
        request.setDescription(jobName);
        request.setExecuteMode(executeMode);
        request.setTimeType(timeType);
        request.setTimeExpression(timeExpression);
        request.setParameters(parameters);

        request.setFailEnable(true);
        request.setSendChannel(sendChannel);
        List<CreateJobRequest.ContactInfo> list=new ArrayList<>();
        CreateJobRequest.ContactInfo info=new CreateJobRequest.ContactInfo();
        info.setUserName(channelUsername);
        info.setUserPhone(channelUserPhone);
        list.add(info);
        request.setContactInfos(list);

        Log.info("定时任务信息"+request);
        Log.info(toString());
        CreateJobResponse response=defaultAcsClient.getAcsResponse(request);

        //保存到数据库
        if(response.getSuccess()) {
            Job job=new Job();
            job.setJobName(jobName);
            job.setStatus(1);
            job.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            job.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
            int i = jobMapper.insertSelective(job);
            Log.info("job:"+jobName+"添加到数据库返回状态为"+i);
        }
        Log.info(response.getMessage()+"------data:"+response.getData()+"------success:"+response.getSuccess()+"------code:"+response.getCode());
        return response;
    }

    @Override
    public DeleteJobResponse deleteJob(long jobId) throws ClientException {

        DefaultAcsClient defaultAcsClient = getDefaultAcsClient();
        DeleteJobRequest request=new DeleteJobRequest();
        request.setGroupId(groupId);
        request.setNamespace(namespace);
        request.setJobId(jobId);
        DeleteJobResponse response=defaultAcsClient.getAcsResponse(request);
        return response;
    }

    @Override
    public EnableJobResponse enableJob(long jobId) throws ClientException {

        DefaultAcsClient defaultAcsClient = getDefaultAcsClient();
        EnableJobRequest enableJobRequest=new EnableJobRequest();
        enableJobRequest.setGroupId(groupId);
        enableJobRequest.setNamespace(namespace);
        enableJobRequest.setJobId(jobId);

        EnableJobResponse response=defaultAcsClient.getAcsResponse(enableJobRequest);
        return response;
    }

    @Override
    public DisableJobResponse disableJob(long jobId) throws ClientException {
        DefaultAcsClient defaultAcsClient = getDefaultAcsClient();
        DisableJobRequest request=new DisableJobRequest();
        request.setGroupId(groupId);
        request.setNamespace(namespace);
        request.setJobId(jobId);

        DisableJobResponse response=defaultAcsClient.getAcsResponse(request);
        return response;
    }

    @Override
    public GetJobInfoResponse getJobInfo(long jobId) throws ClientException {
        DefaultAcsClient defaultAcsClient = getDefaultAcsClient();
        GetJobInfoRequest request=new GetJobInfoRequest();
        request.setJobId(jobId);
        request.setNamespace(namespace);
        request.setGroupId(groupId);
        //发送请求
        GetJobInfoResponse response = defaultAcsClient.getAcsResponse(request);
        return response;
    }

    @Override
    public UpdateJobResponse updateJob(UpdateJobResponse updateJobResponse) {
        return null;
    }

    @Override
    public ExecuteJobResponse executeJob(long jobId) throws ClientException {

        DefaultAcsClient defaultAcsClient = getDefaultAcsClient();
        ExecuteJobRequest request=new ExecuteJobRequest();
        request.setGroupId(groupId);
        request.setNamespace(namespace);
        request.setJobId(jobId);
        ExecuteJobResponse response=defaultAcsClient.getAcsResponse(request);
        return response;
    }

    public DefaultAcsClient getDefaultAcsClient() {

        //构建 OpenApi 客户端
        DefaultProfile.addEndpoint(regionId, productName, domain);
        DefaultProfile defaultProfile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        DefaultAcsClient defaultAcsClient = new DefaultAcsClient(defaultProfile);
        return defaultAcsClient;
    }

    @Override
    public String toString() {
        return "JobServiceImpl{" +
                "regionId='" + regionId + '\'' +
                ", accessKeyId='" + accessKeyId + '\'' +
                ", accessKeySecret='" + accessKeySecret + '\'' +
                ", productName='" + productName + '\'' +
                ", domain='" + domain + '\'' +
                ", namespace='" + namespace + '\'' +
                ", groupId='" + groupId + '\'' +
                ", jobType='" + jobType + '\'' +
                ", className='" + className + '\'' +
                ", executeMode='" + executeMode + '\'' +
                ", timeType=" + timeType +
                ", sendChannel='" + sendChannel + '\'' +
                ", channelUsername='" + channelUsername + '\'' +
                ", channelUserPhone='" + channelUserPhone + '\'' +
                '}';
    }
}
