import React, { useEffect, useState } from 'react';
import { Button } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import FriendList from '../../../Cards/MyPosts/FriendList';
import requestToPath from '../../../Service/fetchService';
import { useUser } from '../../../UserProvider/UserProvider';

const FriendsMain = () => {
    const user = useUser();
    const {userId} = useParams();
    const navigate = useNavigate();
    
    const[friendList, setFriendList] = useState([]);
    const[friendCount, setFriendCount] = useState(0);

    const default_Friend = 'Friends';

    useEffect(() => {
        requestToPath(`/api/friends/user/${userId}`, "GET", user.jwt)
            .then((friendResponse) => {
                setFriendList(friendResponse);
                setFriendCount(friendResponse.length);
            })
            .catch((error) => {console.log(error);})
    }, [friendCount])

    function decreaseCount(){
        const updateCount = friendCount-1;
        setFriendCount(updateCount);
    }

    function displayUserFriend(){
        if(friendList){
            return friendList.sort((a,b) => b.id > a.id ? 1 : -1)
                .map((eachFriend) => {
                    return  <div key={eachFriend.id} >
                                <FriendList key={eachFriend.id} friendId = {eachFriend.id} 
                                firstname={eachFriend.firstname} 
                                lastname={eachFriend.lastname}  
                                selectedFriendId={eachFriend.id}
                                compStatus={default_Friend}
                                emitFriendCount={decreaseCount}
                                />
                            </div> 
                })
        }
        return <div>No friends to Show!</div>
    }

    return (
        <div className='custom-profileWrapper custom-aboutGridEach mt-2 mb-2'>
            <div className='d-flex justify-content-between mb-1'  >
                <h3 className='mb-3'>Friends</h3>
                <Button size='sm' onClick={() => {navigate(`/users/${userId}/friends/allFriends`)}}>More</Button>
            </div>    

            <div className='friendList-eachFriend'>
                {displayUserFriend()}
            </div>
        </div>
    );
};

export default FriendsMain;