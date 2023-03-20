import React, { useEffect, useState } from 'react';
import { Button } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import requestToPath from '../../../Service/fetchService';
import { useUser } from '../../../UserProvider/UserProvider';
import MyPostFollowList from './MyPostFollowList';

import '../Follow/follow.css';
import '../userProfile.css';

const ViewFollowMain = () => {

    const user = useUser();
    const {userId, visitUserId} = useParams();
    const navigate = useNavigate();

    const [pagesList, setPagesList] = useState([]);

    useEffect(() => {
        requestToPath(`/api/pages/user/${visitUserId}/followPages`, "GET", user.jwt)
            .then((followResponse) => { setPagesList(followResponse); })
            .catch((error) => {console.log(error);})
    }, [visitUserId])

    function displayFollowPages(){
        
        if(pagesList != ""){
            return pagesList.sort((a,b) => b.id > a.id ? 1 : -1)
                .map((eachPage) => {
                    return <div key={eachPage.id} className='' ><MyPostFollowList currPage={eachPage} visitUser={visitUserId} /></div> 
                })
        }
        else{
            return <div>Not following pages</div>
        }
    }

    function visitAndDisplayAllPage(){
        navigate(`/users/${userId}/user/${visitUserId}/profile/allPages`, {state: {pagesList}});
    }

    return (
        <div className='custom-profileWrapper mt-2 mb-2'>

            <div className='d-flex justify-content-between mb-1'  >
                <h2 className='mb-3'>Follow</h2>
                <Button size='sm' onClick={() => {visitAndDisplayAllPage()}}>More</Button>        
            </div>

            {/* List of Pages/Games person follows/ */}
            <div className='vfMain-eachPage'>
                {displayFollowPages()}
            </div>
        </div>
    );
};

export default ViewFollowMain;