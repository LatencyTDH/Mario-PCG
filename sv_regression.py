from scipy.io.arff import loadarff
from sklearn.svm import SVR
from sklearn.linear_model import LinearRegression
from sklearn.grid_search import GridSearchCV
import numpy as np
import cPickle
import os
import sys
from optparse import OptionParser

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
                   param_grid={"C": np.logspace(-4,4,9),
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

def build_model(filename, toSave=False, regressionType='sv'):
    X,y = load_dataset(filename)
    if regressionType == 'linear':
        clf = train_linear_model(X,y)
    else:
        clf = train_SVR_model(X,y)
    if toSave:
        save_model(clf)
    return clf

def format_feature_vector(fv):
    """
    Transforms the feature vector from a string into a
    correctly formatted list to be used in the regression model.
    """
    return map(float,list(fv.split(',')))

def main():
    clf = None
    filename = "ratings.arff"

    # parse commandline arguments
    op = OptionParser()
    op.add_option("--rebuild",
              action="store", dest="rebuild_model",
              help="Rebuilds the regression model with new training data. (Linear or SV)")
    op.add_option("--fv", 
        action="store", dest="feature_vector", 
        help="Predict regression score from the inputted feature vector")
    op.add_option("--file", 
        action="store", dest="input_file", 
        help="File that contains the user's funness ratings. Defaults to 'ratings.arff'.")
    (opts, args) = op.parse_args()

    if len(args) > 0:
        op.error("Error with argument format!")
        sys.exit(1)

    if opts.input_file:
        filename = opts.input_file
    if opts.rebuild_model:
        print "Rebuilding %s-regression model on new training data..." % opts.rebuild_model
        if 'linear' in opts.rebuild_model.lower():
            clf = build_model(filename, True, 'linear')
        else:
            clf = build_model(filename, True, 'sv')
        print "Done."
    if opts.feature_vector is not None:
        if os.path.isfile('svr_model.pkl'):
            clf = load_model()
        else:
            clf = build_model(filename, True)
        try:
            fv = format_feature_vector(opts.feature_vector)
            value = clf.predict(fv)
            if isinstance(value, np.ndarray):
                print value[0]
            else:
                print value
        except ValueError as e:
            print e.message

if __name__ == "__main__":
    main()
