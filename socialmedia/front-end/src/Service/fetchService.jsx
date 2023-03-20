
function requestToPath(url, requestMethod, jwt, requestBody){
    const fetchData = {
        headers : {"Content-Type": "application/json"},
        method: requestMethod,
    }

    if(jwt){
        fetchData.headers.Authorization = `Bearer ${jwt}`
    }

    if(requestBody){
        fetchData.body = JSON.stringify(requestBody);
    }
    
    return fetch(url, fetchData) 
        .then((response) => {
            if(response.status === 200){
                return response.json();
            }
            else
                return Promise.reject(response);
        })
}

export default requestToPath;