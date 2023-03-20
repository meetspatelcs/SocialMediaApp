import React, { useEffect, useState } from 'react';
import { Button } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import FriendList from '../../../Cards/MyPosts/FriendList';
import requestToPath from '../../../Service/fetchService';
import { useUser } from '../../../UserProvider/UserProvider';

import '../../../Cards/MyPosts/myPosts.css';
import '../userProfile.css';

const VIewFriendsMain = (props) => {
    const user = useUser();
    const {userId, visitUserId} = useParams();
    const {myFriends} = props
    const navigate = useNavigate();

    const [friendList, setFriendList] = useState([]);
    const [friendCount, setFriendCount] = useState(0);

    const default_null = null;
    
    useEffect(() => {
        requestToPath(`/api/friends/user/${visitUserId}`, "GET", user.jwt)
            .then((friendResponse) => {
                setFriendList(friendResponse);
                setFriendCount(friendResponse.length);
            })
            .catch((error) => {console.log(error);})
    }, [visitUserId])

    function validateFriendStatus(currId){
        const temp = myFriends.find(f => f.id == currId);
        if(temp != null){ return temp.status; }
        return default_null;
    }

    function displayUserFriend(){
        if(friendList){
            return friendList.sort((a,b) => b.id > a.id ? 1 : -1)
                .map((eachFriend) => {
                    return <div key={eachFriend.id} className='' >
                                <FriendList key={eachFriend.id} friendId = {eachFriend.id} 
                                    firstname={eachFriend.firstname} 
                                    lastname={eachFriend.lastname}  
                                    selectedFriendId={eachFriend.id} 
                                    // emitFriendCount={decreaseCount} 
                                    compStatus={validateFriendStatus(eachFriend.id)}
                                    myFriends={myFriends}
                                    />
                            </div> 
                })
        }
        return <div>No friends to Show!</div>
    }

    function visitAndDisplayFriends(){
        navigate(`/users/${userId}/user/${visitUserId}/profile/allFriends`, {state: {friendList}})
    }

    return (
        <div className='custom-profileWrapper mt-2 mb-2'>
            <div className='d-flex justify-content-between mb-1'  >
                <h3 className='mb-3'>Friends</h3>
                <Button size='sm' onClick={() => {visitAndDisplayFriends()}}>More</Button>
            </div>    
            <div className='friendList-eachFriend'>
                {displayUserFriend()}
            </div>
    </div>
    );
};

export default VIewFriendsMain;