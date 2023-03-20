function testUpload(url, requestMethod, jwt, requestBody){
    const fetchData = {
        headers : {},
        method: requestMethod,
    }

    if(jwt){
        fetchData.headers.Authorization = `Bearer ${jwt}`
    }

    if(requestBody){
        fetchData.body = requestBody;
    }
    
    return fetch(url, fetchData) 
        .then((response) => {
            if(response.status === 200)
                return response.json();
        })
}

export default testUpload;