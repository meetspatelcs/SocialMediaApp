import React, { useEffect, useState } from 'react';
import { LazyLoadImage } from 'react-lazy-load-image-component';
import { useUser } from '../../UserProvider/UserProvider';

const ViewVisitUserImg = (props) => {
    const user = useUser();
    const {currPostId} = props;

    const [myUrl, setMyUrl] = useState(null);

    useEffect(() => {
        fetch(`/api/posts/${currPostId}/postImages`, {headers: {Authorization: `Bearer ${user.jwt}`}, method: "GET"})
            .then((dataBuffer) => {
                return dataBuffer.blob();
            })
            .then((data) => { 
                setMyUrl(URL.createObjectURL(data));
                return data;
            })
            .catch((error) => {console.log(error);})
    }, [])
    
    function displayImg(){
        return <LazyLoadImage  className='p-1' 
                src={myUrl}
                style={{maxWidth:"100%", maxHeight:"32rem", width:"auto", height:"auto"}} alt="Image Alt" />
    }

    return (
        <>
            {displayImg()}
        </>
        
    );
};

export default ViewVisitUserImg;