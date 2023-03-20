import React, { useEffect, useState } from 'react';
import { Button } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import ViewMyPostsMedia from '../../../Cards/ViewMyPostsMedia';
import requestToPath from '../../../Service/fetchService';
import { useUser } from '../../../UserProvider/UserProvider';

import '../Media/media.css';
const MediaMain = () => {
    const user = useUser();
    const {userId} = useParams();
    const navigate = useNavigate();

    const[myImgList, setMyImgList] = useState([]);

    useEffect(() => {
        requestToPath(`/api/posts/myPosts/imgList`, "GET", user.jwt)
            .then((imgResponse) => { setMyImgList(imgResponse); })
            .catch((error) => {console.log(error);})
    }, [])

    function displayMedia(){
        if(myImgList != ''){
            return myImgList.sort((a,b) => b.id > a.id ? 1 : -1)
                .map((eachImg) => {
                    return <ViewMyPostsMedia key={eachImg.id} currPostImg={eachImg}  />;
                })
        }
        return <div>No media yet!</div>
    }

    return (
        <div className='custom-profileWrapper custom-aboutGridEach mt-2 mb-2' >
            <div className='d-flex justify-content-between mb-1'  >
                <h3 className='mb-3'>Media</h3>
                <Button size='sm' onClick={() => {navigate(`/users/${userId}/myPosts/allImg`, {state: {myImgList}})}}>More</Button>
            </div>

            <div className='vmdiMain-eachMedia'>
                {displayMedia()}
            </div>
        </div>
    );
};

export default MediaMain;