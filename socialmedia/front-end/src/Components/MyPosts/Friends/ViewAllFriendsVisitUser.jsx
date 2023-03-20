import React, { useState } from 'react';
import { Button } from 'react-bootstrap';
import {useLocation, useNavigate, useParams} from 'react-router-dom';
import ViewAllFriends from '../../../Cards/MyPosts/UserViewOn/ViewAllFriends';

import './friends.css';

const ViewAllFriendsVisitUser = () => {
    const {state} = useLocation();
    const {userId, visitUserId} = useParams();
    const navigate = useNavigate();

    const [userList, setUserList] = useState([]);

    useState(() => {
        if(state){  
            setUserList(state.friendList);
        }
    }, [state])


    function displayUser(){
        if(userList != ''){
            return userList.map((eachUser) => {
                return <ViewAllFriends key={eachUser.id} currUser={eachUser}/>
            })
        }
    }

    function handleClick(){
        navigate(-1);
    }

    return (
        <div>
            <div className='viewAllFVU-fList vAllFriend-mainBox justify-content-center align-items-center'>
                {displayUser()}
            </div>
            <div className='vAllFriend-Backbox'>
                <Button onClick={handleClick}>Back To Profile</Button>
            </div>
        </div>
    );
};

export default ViewAllFriendsVisitUser;