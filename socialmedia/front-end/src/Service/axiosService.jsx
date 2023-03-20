import axios from 'axios';

function backendService(requestMethod, url, jwt, requestBody){
    const axiosData = {
        headers: {},
        method: requestMethod,
    }

    if(jwt){
        axiosData.headers.Authorization = `Bearer ${jwt}`
    }

    if(requestBody){
        axiosData.body = requestBody
    }

    return axios(url, axiosData)
        .then((response) => {
            if(response.status === 200)
                return response.json();
        })
}

export default backendService;