import React, { useEffect, useState } from 'react';
import { Image } from 'react-bootstrap';
import { useUser } from '../UserProvider/UserProvider';

import '../Components/MyPosts/Media/media.css';

const ViewMyPostsMedia = (props) => {
    const user = useUser();
    const {currPostImg} = props;
    
    const [myurl, setMyUrl] = useState();
    const postId = currPostImg.id;
    
    useEffect(() => {
        fetch(`/api/posts/${postId}/postImages`, {headers: {Authorization: `Bearer ${user.jwt}`}, method: "GET"})
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
        if(myurl != null || myurl != ''){
            return <Image className='viewMPM-imgSize' src={myurl}  />;
        }
    }

    return (
        <div className='img-center'>
            {displayImg()}
        </div>
    );
};

export default ViewMyPostsMedia;