import React, { useEffect, useState } from 'react';
import { Button } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import ViewMyPostsMedia from '../../../Cards/ViewMyPostsMedia';
import requestToPath from '../../../Service/fetchService';
import { useUser } from '../../../UserProvider/UserProvider';

import '../Media/media.css';
import '../userProfile.css';

const ViewMediaMain = () => {
    const user = useUser();
    const {userId, visitUserId} =  useParams();
    const navigate = useNavigate();

    const[userImgList, setUserImgList] = useState([]);

    useEffect(() => {
        requestToPath(`/api/posts/user/${visitUserId}/profile/imgList`, "GET", user.jwt)
            .then((imgResponse) => {setUserImgList(imgResponse);})
            .catch((error) => {console.log(error)})
    }, [visitUserId])

    function displayMedia(){
        if(userImgList != ''){
            return userImgList.sort((a,b) => b.id > a.id ? 1 : -1)
                .map((eachImg) => {
                    return <ViewMyPostsMedia key={eachImg.id} currPostImg={eachImg} />;
                })
        }
        return <div>No img found</div>
    }

    function visitAndDisplayAllImg(){
        navigate(`/users/${userId}/user/${visitUserId}/profile/allImg`, {state: {userImgList}});
    }

    return (
        <div className='custom-profileWrapper custom-aboutGridEach mt-2 mb-2' >
            <div className='d-flex justify-content-between mb-1'  >
                <h3 className='mb-3'>Media</h3>
                <Button size='sm' onClick={() => {visitAndDisplayAllImg()}}>More</Button>
            </div>

            <div className='vmdiMain-eachMedia'>
                {displayMedia()}
            </div>
        </div>
    );
};

export default ViewMediaMain;