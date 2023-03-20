import React, { useEffect, useState } from 'react';
import { Button } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import requestToPath from '../../../Service/fetchService';
import { useUser } from '../../../UserProvider/UserProvider';
import MyPostFollowList from './MyPostFollowList';

const FollowMain = () => {
    const user = useUser();
    const {userId} = useParams();
    const navigate = useNavigate();
    const visitUser = null;

    const [pagesList, setPagesList] = useState([]);

    useEffect(() => {
        requestToPath(`/api/pages/followingPages`, "GET", user.jwt)
            .then((followResponse) => { setPagesList(followResponse); })
            .catch((error) => {console.log(error);})
    }, [])

    function displayFollowPages(){
        if(pagesList != ''){
            return pagesList.sort((a,b) => b.id > a.id ? 1 : -1)
                .map((eachPage) => {
                    return <div key={eachPage.id} ><MyPostFollowList currPage={eachPage} visitUser={visitUser} /></div> 
                })
        }        
        return <div className='text-center'>No pages to show</div>
    }

    return (
        <div className='custom-profileWrapper custom-aboutGridEach mt-2 mb-2'>

            <div className='d-flex justify-content-between mb-1'  >
                <h2 className='mb-3'>Follow</h2>
                <Button size='sm' onClick={() => {navigate(`/users/${userId}/pages`)}}>More</Button>        
            </div>

            {/* List of Pages/Games person follows/ */}
            <div className='vfMain-eachPage'>
                {displayFollowPages()}
            </div>
        </div>
    );
};

export default FollowMain;