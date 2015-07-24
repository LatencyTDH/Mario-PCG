from scipy.io.arff import loadarff
from sklearn.svm import SVR
from sklearn.linear_model import LinearRegression
from sklearn.grid_search import GridSearchCV
import numpy as np
import cPickle
import os
import sys

# loads a .arff data file
def load_dataset(filename):
    """
    Returns an array of samples X and the class labels y.
    """
    dataset = loadarff(open(filename,'r'))
    features = dataset[1].names()
    class_attr = features[-1]
    y = np.array(dataset[0][class_attr])
    X = np.array(dataset[0][features[:-1]])
    X = np.array(map(lambda x: list(x), X))
    return X,y

def train_SVR_model(X,y):
    """
    Returns the CV-fitted SVM model according to the given training data.
    """
    clf = GridSearchCV(SVR(kernel='rbf', gamma=0.1), cv=5,
                   param_grid={"C": [1e0, 1e1, 1e2, 1e3],
                               "gamma": np.logspace(-2, 2, 5)})
    clf.fit(X,y)
    return clf

def train_linear_model(X,y):
    lr = LinearRegression()
    lr.fit(X,y)
    return lr

def save_model(classifier):
    cPickle.dump(classifier, open('svr_model.pkl','wb'))

def load_model():
    """
    Loads the pickled file holding the trained regression model.
    """
    return cPickle.load(open('svr_model.pkl'))

def count_datalines(filename):
    with open(filename,'r') as f:
        text = f.read()
    data_idx = text.index("@data")
    return text[data_idx:].count('\n') - 1

def main():
    clf = None
    test_predict = [0.000000,0.000000,0.410799,0.426060,0.163142,3,3,15,2,70]

    if os.path.isfile('svr_model.pkl'):
        clf = load_model()
    else:
        X,y = load_dataset("myratings.arff")
        clf = train_SVR_model(X,y)
        save_model(clf)

    if sys.argv > 1:
        print sys.argv

    print clf.predict(test_predict)

if __name__ == "__main__":
    # main()
    print count_datalines('myratings.arff')
    print count_datalines('ratings.txt')
    # X,y = load_dataset("myratings.arff")
    # clf = train_linear_model(X,y)
    # print clf.predict([1.000000,0.000000,0.410799,0.426060,0.163142,3,3,15,2,2])

# X,y = load_dataset("myratings.arff")
# clf = train_SVR_model(X,y)
# cPickle.dump(clf, open('svr_model.pkl','wb'))
