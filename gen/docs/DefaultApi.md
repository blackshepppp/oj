# DefaultApi

All URIs are relative to *https://yuoj_backend_microservice*

Method | HTTP request | Description
------------- | ------------- | -------------
[**imgUpload**](DefaultApi.md#imgUpload) | **POST** /upload | POST upload


<a name="imgUpload"></a>
# **imgUpload**
> BaseResponse imgUpload(file)

POST upload

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://yuoj_backend_microservice");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    File file = new File("/path/to/file"); // File | 
    try {
      BaseResponse result = apiInstance.imgUpload(file);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#imgUpload");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **file** | **File**|  |

### Return type

[**BaseResponse**](BaseResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

